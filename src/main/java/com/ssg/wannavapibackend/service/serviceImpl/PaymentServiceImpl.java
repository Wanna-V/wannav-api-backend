package com.ssg.wannavapibackend.service.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.wannavapibackend.common.ErrorCode;
import com.ssg.wannavapibackend.common.PaymentCancelReason;
import com.ssg.wannavapibackend.common.Status;
import com.ssg.wannavapibackend.config.TossPaymentConfig;
import com.ssg.wannavapibackend.domain.Payment;
import com.ssg.wannavapibackend.domain.PaymentItem;
import com.ssg.wannavapibackend.domain.PointLog;
import com.ssg.wannavapibackend.domain.Product;
import com.ssg.wannavapibackend.domain.User;
import com.ssg.wannavapibackend.domain.UserCoupon;
import com.ssg.wannavapibackend.dto.PaymentRefundDTO;
import com.ssg.wannavapibackend.dto.request.ProductPurchaseRequestDTO;
import com.ssg.wannavapibackend.dto.request.PaymentConfirmRequestDTO;
import com.ssg.wannavapibackend.dto.request.TossPaymentRequestDTO;
import com.ssg.wannavapibackend.dto.response.AvailableUserCouponResponseDTO;
import com.ssg.wannavapibackend.dto.response.CheckoutResponseDTO;
import com.ssg.wannavapibackend.dto.response.PaymentConfirmResponseDTO;
import com.ssg.wannavapibackend.dto.response.PaymentItemResponseDTO;
import com.ssg.wannavapibackend.dto.response.PaymentResponseDTO;
import com.ssg.wannavapibackend.exception.CustomException;
import com.ssg.wannavapibackend.exception.PaymentCancelException;
import com.ssg.wannavapibackend.facade.RedissonLockStockFacade;
import com.ssg.wannavapibackend.repository.PaymentItemRepository;
import com.ssg.wannavapibackend.repository.PaymentRepository;
import com.ssg.wannavapibackend.repository.PointLogRepository;
import com.ssg.wannavapibackend.repository.ProductRepository;
import com.ssg.wannavapibackend.repository.UserCouponRepository;
import com.ssg.wannavapibackend.repository.UserRepository;
import com.ssg.wannavapibackend.service.PaymentService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final TossPaymentConfig tossPaymentConfig;
    private final ObjectMapper objectMapper;
    private final PaymentRepository paymentRepository;
    private final PaymentItemRepository paymentItemRepository;
    private final UserRepository userRepository;
    private final UserCouponRepository userCouponRepository;
    private final PointLogRepository pointLogRepository;
    private final ProductRepository productRepository;
    private final RedissonLockStockFacade redissonLockStockFacade;


    @Override
    public CheckoutResponseDTO processCartCheckout(Long userId, List<Long> cartIds) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<PaymentItemResponseDTO> itemList = paymentRepository.findCartsForPayment(userId,
            cartIds);

        List<UserCoupon> userCoupons = userCouponRepository.findAllByUserIdAndEndDate(userId);
        List<AvailableUserCouponResponseDTO> availableUserCoupons = userCoupons.stream()
            .map(userCoupon -> AvailableUserCouponResponseDTO.builder()
                .id(userCoupon.getCoupon().getId())
                .code(userCoupon.getCoupon().getCode())
                .name(userCoupon.getCoupon().getName())
                .type(userCoupon.getCoupon().getType())
                .discountAmount(userCoupon.getCoupon().getDiscountAmount())
                .discountRate(userCoupon.getCoupon().getDiscountRate())
                .endDate(userCoupon.getCoupon().getEndDate())
                .build())
            .collect(Collectors.toList());

        return CheckoutResponseDTO.builder()
            .clientKey(tossPaymentConfig.getTossClientKey())
            .name(user.getName())
            .phone(user.getPhone())
            .address(user.getAddress())
            .email(user.getEmail())
            .point(user.getPoint())
            .coupons(availableUserCoupons)
            .products(itemList)
            .build();
    }

    @Override
    public CheckoutResponseDTO processDirectProductCheckout(Long userId,
        ProductPurchaseRequestDTO productRequestDTO) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Product product = productRepository.findById(productRequestDTO.getProductId())
            .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        List<UserCoupon> userCoupons = userCouponRepository.findAllByUserIdAndEndDate(userId);
        List<AvailableUserCouponResponseDTO> availableUserCoupons = userCoupons.stream()
            .map(userCoupon -> AvailableUserCouponResponseDTO.builder()
                .id(userCoupon.getCoupon().getId())
                .code(userCoupon.getCoupon().getCode())
                .name(userCoupon.getCoupon().getName())
                .type(userCoupon.getCoupon().getType())
                .discountAmount(userCoupon.getCoupon().getDiscountAmount())
                .discountRate(userCoupon.getCoupon().getDiscountRate())
                .endDate(userCoupon.getCoupon().getEndDate())
                .build())
            .collect(Collectors.toList());

        PaymentItemResponseDTO item = PaymentItemResponseDTO.builder()
            .id(product.getId())
            .image(product.getImage())
            .name(product.getName())
            .quantity(productRequestDTO.getQuantity())
            .paymentPrice(product.getFinalPrice() * productRequestDTO.getQuantity())
            .build();

        return CheckoutResponseDTO.builder()
            .clientKey(tossPaymentConfig.getTossClientKey())
            .name(user.getName())
            .phone(user.getPhone())
            .address(user.getAddress())
            .email(user.getEmail())
            .point(user.getPoint())
            .coupons(availableUserCoupons)
            .products(Collections.singletonList(item))
            .build();
    }

    /**
     * 결제 번호 생성 결제일(YYYYMMDD) + 랜덤 8자리 숫자
     */
    @Override
    public PaymentResponseDTO generateOrderId() {
        // 결제일(YYYYMMDD) 가져오기
        String dateStr = new SimpleDateFormat("yyyyMMdd").format(new Date());

        // 랜덤 8자리 숫자 생성
        int randomNumber = 10000000 + new Random().nextInt(90000000);

        // 결제일과 랜덤 숫자를 결합하여 결제번호 생성
        String orderId = dateStr + randomNumber;

        return PaymentResponseDTO.builder()
            .orderId(orderId)
            .successUrl(tossPaymentConfig.getSuccessUrl())
            .failUrl(tossPaymentConfig.getFailUrl())
            .build();
    }

    @Transactional
    public PaymentConfirmResponseDTO sendRequest(Long userId, PaymentConfirmRequestDTO requestDTO) {
        PaymentConfirmResponseDTO confirmResponseDTO = processPaymentConfirmation(
            requestDTO.getTossPaymentRequestDTO());
        PaymentCancelReason cancelReason = null;

        // 결제 확인 요청이 성공일 경우 재고 감소 로직 실행
        if (Status.DONE.toString().equalsIgnoreCase(confirmResponseDTO.getStatus().toString())) {
            /**
             * 결제 상품 수량 감소
             */
            List<ProductPurchaseRequestDTO> productRequestDTOList = requestDTO.getPaymentItemRequestDTO()
                .getProducts().stream()
                .map(item -> ProductPurchaseRequestDTO.builder()
                    .productId(item.getProductId())
                    .quantity(item.getQuantity())
                    .build())
                .collect(Collectors.toList());

            try {
                // 1. 사용자 조회
                User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

                // 2. 상품 재고 감소
                try {
                    redissonLockStockFacade.decreaseProductStock(productRequestDTOList);
                } catch (Exception e) {
                    cancelReason = PaymentCancelReason.STOCK_INSUFFICIENT;
                    throw e;
                }

                // 3. 결제 정보 저장
                try {
                    saveProductPayment(user, requestDTO, confirmResponseDTO);
                } catch (Exception e) {
                    cancelReason = PaymentCancelReason.PAYMENT_SAVE_FAILED;
                    throw e;
                }

                // 4. 쿠폰 상태 업데이트
                try {
                    userCouponRepository.updateCouponStatus(true, userId,
                        requestDTO.getPaymentItemRequestDTO().getCouponId());
                } catch (Exception e) {
                    cancelReason = PaymentCancelReason.COUPON_UPDATE_FAILED;
                    throw e;
                }

                // 5. 포인트 로그
                Integer pointsUsed = requestDTO.getPaymentItemRequestDTO().getPointsUsed();
                if (pointsUsed != null && pointsUsed > 0) {
                    try {
                        savePointLog(user, pointsUsed);
                    } catch (Exception e) {
                        cancelReason = PaymentCancelReason.POINT_LOG_FAILED;
                        throw e;
                    }
                }
            } catch (Exception e) {
                PaymentRefundDTO refundDTO = PaymentRefundDTO.builder()
                    .paymentKey(requestDTO.getTossPaymentRequestDTO().getPaymentKey())
                    .cancelReason(
                        cancelReason != null ? cancelReason : PaymentCancelReason.UNKNOWN_ERROR)
                    .cancelAmount(requestDTO.getTossPaymentRequestDTO().getAmount())
                    .build();

                log.error("Payment process failed, canceling payment. Reason: {}",
                    cancelReason != null ? cancelReason : PaymentCancelReason.UNKNOWN_ERROR, e);
                requestPaymentCancel(refundDTO);
                throw new PaymentCancelException(cancelReason);
            }
        }

        return confirmResponseDTO;
    }

    /**
     * 결제 확인 요청을 처리하는 메서드.
     *
     * @param requestDTO 결제 확인에 필요한 데이터가 담긴 요청 DTO.
     * @return 결제 확인 응답을 담은 DTO 객체.
     * @throws IOException IO 예외가 발생할 수 있음.
     */
    private PaymentConfirmResponseDTO processPaymentConfirmation(
        TossPaymentRequestDTO requestDTO) {
        try {
            String confirmUrl = tossPaymentConfig.getUrl() + "confirm";

            // 결제 확인 요청을 위한 HTTP 연결 설정
            HttpURLConnection connection = createConnection(confirmUrl);

            // 요청 데이터(requestDTO)를 JSON 문자열로 변환하여 HTTP 요청 본문에 포함시켜 전송
            try (OutputStream os = connection.getOutputStream()) {
                os.write(requestDTO.toJson().getBytes(StandardCharsets.UTF_8));
            }

            // 응답을 받아와서 JSON 스트림을 읽고, 그 데이터를 PaymentConfirmResponseDTO로 변환
            try (InputStream responseStream = connection.getResponseCode() == 200
                ? connection.getInputStream()  // 응답 코드가 200이면 정상 응답 스트림 사용
                : connection.getErrorStream(); // 아니면 오류 응답 스트림 사용
                Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8)) {
                // JSON 응답을 PaymentConfirmResponseDTO 객체로 변환
                return objectMapper.readValue(reader, PaymentConfirmResponseDTO.class);
            } catch (Exception e) { // 응답 읽기 오류가 발생
                log.error("Error reading response", e);// 예외 발생 시, 오류 상태를 반환하는 DTO 객체 생성

                return PaymentConfirmResponseDTO.builder()
                    .status(Status.ABORTED)
                    .message("Error reading response: " + e)
                    .build();
            }
        } catch (IOException e) { // 연결 생성 중 오류 발생
            log.error("IOException in sendRequest", e);
            throw new CustomException(ErrorCode.PAYMENT_CONNECTION_FAILED);
        } catch (Exception e) { // 예기치 못한 오류
            log.error("Unexpected error", e);
            throw new CustomException(ErrorCode.PAYMENT_UNKNOWN_ERROR);
        }
    }

    /**
     * HTTP 연결을 생성하는 메서드.
     *
     * @return 생성된 HttpURLConnection 객체.
     * @throws IOException 연결을 설정하는 과정에서 IO 예외가 발생할 수 있음.
     */
    private HttpURLConnection createConnection(String connectionUrl) {
        try {
            // Toss 결제 API URL을 가져와 URL 객체 생성
            URL url = new URL(connectionUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 인증 헤더 설정: Widget Secret Key를 Base64 인코딩하여 요청 헤더에 추가
            connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder()
                .encodeToString((tossPaymentConfig.getWidgetSecretKey() + ":").getBytes(
                    StandardCharsets.UTF_8)));

            // 요청 본문의 데이터 타입을 JSON으로 설정
            connection.setRequestProperty("Content-Type", "application/json");

            // HTTP 요청 방식 설정: POST 방식
            connection.setRequestMethod("POST");

            // 요청 본문을 전송할 수 있도록 설정
            connection.setDoOutput(true);

            // 설정된 connection 객체를 반환
            return connection;
        } catch (IOException e) { // 연결 생성 실패
            log.error("Error creating connection", e);
            throw new CustomException(ErrorCode.PAYMENT_CONNECTION_FAILED);
        } catch (Exception e) { // 예기치 못한 오류
            log.error("Unexpected error in connection creation", e);
            throw new CustomException(ErrorCode.PAYMENT_UNKNOWN_ERROR);
        }
    }

    /**
     * 결제 상품 수량 감소
     *
     * @param productId - 상품
     * @param quantity  - 수량
     */
    @Transactional
    public void decrease(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        product.decrease(quantity);

        productRepository.save(product);
    }

    /**
     * 결제 정보(Payment) & 결제 상품(PaymentItem) 데이터 저장
     *
     * @param user               - 유저
     * @param confirmRequestDTO  - 결제 및 결제 상품 정보
     * @param confirmResponseDTO - 결제 승인 정보
     */
    @Transactional
    protected void saveProductPayment(User user, PaymentConfirmRequestDTO confirmRequestDTO,
        PaymentConfirmResponseDTO confirmResponseDTO) {
        try {

            Payment savePayment = Payment.builder()
                .user(user)
                .paymentKey(confirmRequestDTO.getTossPaymentRequestDTO().getPaymentKey())
                .orderId(confirmRequestDTO.getTossPaymentRequestDTO().getOrderId())
                .actualPrice(confirmRequestDTO.getPaymentItemRequestDTO().getActualPrice())
                .finalPrice(confirmRequestDTO.getTossPaymentRequestDTO().getAmount())
                .pointsUsed(confirmRequestDTO.getPaymentItemRequestDTO().getPointsUsed())
                .finalDiscountRate(
                    confirmRequestDTO.getPaymentItemRequestDTO().getFinalDiscountRate())
                .finalDiscountAmount(
                    confirmRequestDTO.getPaymentItemRequestDTO().getFinalDiscountAmount())
                .couponCode(confirmRequestDTO.getPaymentItemRequestDTO().getCouponCode())
                .status(confirmResponseDTO.getStatus())
                .address(confirmRequestDTO.getPaymentItemRequestDTO().getAddress())
                .note(confirmRequestDTO.getPaymentItemRequestDTO().getNote())
                .createdAt(confirmRequestDTO.getPaymentItemRequestDTO().getCreatedAt())
                .approvedAt(confirmResponseDTO.getApprovedAt())
                .build();

            paymentRepository.save(savePayment);

            Payment payment = paymentRepository.findByOrderId(
                    confirmRequestDTO.getTossPaymentRequestDTO().getOrderId())
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

            List<PaymentItem> paymentItems = confirmRequestDTO.getPaymentItemRequestDTO()
                .getProducts().stream().map(item -> {
                    Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

                    return PaymentItem.builder()
                        .payment(payment)
                        .product(product)
                        .quantity(item.getQuantity())
                        .build();
                }).collect(Collectors.toList());

            paymentItemRepository.saveAll(paymentItems);
        } catch (
            Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ErrorCode.PAYMENT_SAVE_FAILED);
        }
    }

    /**
     * 결제 시, 사용한 포인트 로그 생성
     *
     * @param user      - 유저
     * @param usedPoint - 사용한 포인트
     */
    @Transactional
    protected void savePointLog(User user, int usedPoint) {
        PointLog oldPointLog = pointLogRepository.findFirstByUserIdOrderByCreatedAtDesc(
            user.getId()).orElseThrow(() -> new CustomException(ErrorCode.POINT_LOG_NOT_FOUND));

        // 새로운 포인트가 음수로 계산되는 경우 예외 처리
        int newPoint = oldPointLog.getNewPoint() - usedPoint;
        if (newPoint < 0) {
            throw new CustomException(ErrorCode.INSUFFICIENT_POINTS);
        }

        PointLog newPointLog = PointLog.builder()
            .user(user)
            .oldPoint(oldPointLog.getNewPoint())
            .newPoint(newPoint)
            .createdAt(LocalDateTime.now())
            .build();

        user.updatePoint(newPoint);

        pointLogRepository.save(newPointLog);
        userRepository.save(user);
    }

    /**
     * 결제 취소
     *
     * @param requestDTO
     * @return
     */
    @Transactional
    protected PaymentRefundDTO requestPaymentCancel(PaymentRefundDTO requestDTO) {
        try {
            String cancelUrl = tossPaymentConfig.getUrl() + requestDTO.getPaymentKey() + "/cancel";

            // 결제 확인 요청을 위한 HTTP 연결 설정
            HttpURLConnection connection = createConnection(cancelUrl);

            // 요청 데이터(requestDTO)를 JSON 문자열로 변환하여 HTTP 요청 본문에 포함시켜 전송
            try (OutputStream os = connection.getOutputStream()) {
                String requestBody = requestDTO.toJson();
                log.info("Cancel Request Body: {}", requestBody);
                os.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            // 응답 읽기
            int responseCode = connection.getResponseCode();
            log.info("Cancel API Response Code: {}", responseCode);

            // 응답을 받아와서 JSON 스트림을 읽고, 그 데이터를 PaymentConfirmResponseDTO로 변환
            try (InputStream responseStream = responseCode == 200
                ? connection.getInputStream()  // 응답 코드가 200이면 정상 응답 스트림 사용
                : connection.getErrorStream();) // 아니면 오류 응답 스트림 사용
            {
                String response = new BufferedReader(new InputStreamReader(responseStream))
                    .lines()
                    .collect(Collectors.joining("\n"));

                log.info("Cancel API Response: {}", response);

                return objectMapper.readValue(response, PaymentRefundDTO.class);

            } catch (Exception e) { // 응답 읽기 오류가 발생
                log.error("Error reading response", e);// 예외 발생 시, 오류 상태를 반환하는 DTO 객체 생성

                return PaymentRefundDTO.builder()
                    .status(Status.ABORTED)
                    .message("Error reading response: " + e)
                    .build();
            }
        } catch (IOException e) { // 연결 생성 중 오류 발생
            log.error("IOException in sendRequest", e);
            throw new CustomException(ErrorCode.PAYMENT_CONNECTION_FAILED);
        } catch (Exception e) { // 예기치 못한 오류
            log.error("Unexpected error", e);
            throw new CustomException(ErrorCode.PAYMENT_UNKNOWN_ERROR);
        }
    }
}
