package com.ssg.wannavapibackend.controller.web;

import com.ssg.wannavapibackend.dto.request.ProductCheckoutRequestDTO;
import com.ssg.wannavapibackend.dto.request.PaymentItemRequestDTO;
import com.ssg.wannavapibackend.dto.response.CheckoutResponseDTO;
import com.ssg.wannavapibackend.dto.response.ReservationPaymentResponseDTO;
import com.ssg.wannavapibackend.facade.CheckoutFacade;
import com.ssg.wannavapibackend.service.ReservationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/checkout")
public class PaymentController {

    private final CheckoutFacade checkoutFacade;
    private final ReservationService reservationService;
    final Long userId = 1L; // Security 적용 후 삭제 예정

    /**
     * (상품 페이지 OR 장바구니 페이지) 에서 결제 이동 시, 상품 정보를 세션으로 전달하기 위한 메서드
     */
    @PostMapping("/product")
    public String redirectToProductPaymentPage(
        @RequestBody ProductCheckoutRequestDTO checkoutRequestDTO,
        HttpSession session) {

        CheckoutResponseDTO responseDTO = checkoutFacade.processCheckout(userId,
            checkoutRequestDTO);
        session.setAttribute("pageInitData", responseDTO);

        return "redirect:/checkout/product";
    }

    @GetMapping("/product")
    public String showProductPaymentPage(HttpSession session, Model model) {
        CheckoutResponseDTO responseDTO = (CheckoutResponseDTO) session.getAttribute(
            "pageInitData");

        model.addAttribute("pageInitData", responseDTO);

        return "payment/product";
    }


    @GetMapping("/reservation/{reservationId}")
    public String reservationPayment(@PathVariable Long reservationId, Model model) {

        ReservationPaymentResponseDTO reservationPaymentResponseDTO = reservationService.getReservationPayment(
            reservationId);

        model.addAttribute("reservationPaymentResponseDTO", reservationPaymentResponseDTO);

        return "/payment/reservation";
    }

    /**
     * 상품 결제 정보를 세션에 저장하고, 결제 승인 페이지로 리다이렉트하는 메서드
     */
    @PostMapping("/product-data")
    public String storeProductPaymentDataAndRedirectToSuccessPage(
        @RequestBody PaymentItemRequestDTO requestDTO,
        HttpSession session) {
        log.info("requestDTO: " + requestDTO.toString());
        session.setAttribute("paymentItemData", requestDTO);

        return "redirect:/checkout/toss-success";
    }

    @GetMapping("/toss-success")
    public String paymentSuccess(HttpSession session, Model model) {
        log.info("paymentSuccess");
        PaymentItemRequestDTO requestDTO = (PaymentItemRequestDTO) session.getAttribute("paymentItemData");
        log.info("paymentSuccess requestDTO" + requestDTO.toString());

        model.addAttribute("paymentItemData", requestDTO);

        return "/payment/toss-success";
    }

    @GetMapping("/toss-fail")
    public String failPayment(HttpServletRequest request, Model model) {
        model.addAttribute("code", request.getParameter("code"));
        model.addAttribute("message", request.getParameter("message"));
        return "/payment/toss-fail";
    }

//    @GetMapping("/success")
//    public String reservationPaySuccess(
//        @RequestParam(value = "orderId") String orderId,
//        @RequestParam(value = "amount") Integer amount,
//        @RequestParam(value = "paymentKey") String paymentKey) {
//        return "redirect:/success";
//    }
//
//    @GetMapping("/fail")
//    public String reservationPayFail(
//        @RequestParam(value = "orderId") String orderId,
//        @RequestParam(value = "amount") Integer amount,
//        @RequestParam(value = "paymentKey") String paymentKey) {
//        return "redirect:/success";
//    }
}
