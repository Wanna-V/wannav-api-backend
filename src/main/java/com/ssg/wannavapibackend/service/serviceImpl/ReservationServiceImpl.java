package com.ssg.wannavapibackend.service.serviceImpl;

import com.ssg.wannavapibackend.domain.Reservation;
import com.ssg.wannavapibackend.domain.Restaurant;
import com.ssg.wannavapibackend.domain.Seat;
import com.ssg.wannavapibackend.domain.User;
import com.ssg.wannavapibackend.dto.request.ReservationRequestDTO;
import com.ssg.wannavapibackend.dto.response.ReservationDTO;
import com.ssg.wannavapibackend.dto.response.ReservationPaymentResponseDTO;
import com.ssg.wannavapibackend.dto.response.ReservationSaveResponseDTO;
import com.ssg.wannavapibackend.dto.response.ReservationDateResponseDTO;
import com.ssg.wannavapibackend.repository.ReservationRepository;
import com.ssg.wannavapibackend.repository.RestaurantRepository;
import com.ssg.wannavapibackend.repository.UserRepository;
import com.ssg.wannavapibackend.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    /**
     * 한 유저의 예약 전체 조회(마이페이지)
     */
    @Override
    public List<ReservationDTO> getReservationList(Long userId) {
        List<Reservation> reservations = reservationRepository.findAllByUserId(userId);

        for(Reservation reservation : reservations)
            log.info(reservation);

        return reservations.stream()
                .map(reservation -> new ReservationDTO(
                        reservation.getId(),
                        reservation.getUser(),
                        reservation.getRestaurant(),
                        reservation.getPayment(),
                        reservation.getGuest(),
                        reservation.getReservationDate(),
                        reservation.getReservationTime()
                )).collect(Collectors.toList());
    }

    /**
     * 예약 상세 조회
     */
    @Override
    public ReservationDTO getReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new IllegalArgumentException("Invalid ID value: " + reservationId));

        return new ReservationDTO(
                reservation.getId(),
                reservation.getUser(),
                reservation.getRestaurant(),
                reservation.getPayment(),
                reservation.getGuest(),
                reservation.getReservationDate(),
                reservation.getReservationTime()
        );
    }

    /**
     * 예약하기를 눌렀을 때 저장된 예약 데이터 조회
     */
    @Override
    public ReservationPaymentResponseDTO getReservationPayment(Long reservationId) {
        Reservation reservation = reservationRepository.findByReservationId(reservationId);

        LocalDateTime dateTime = LocalDateTime.of(reservation.getReservationDate(), reservation.getReservationTime());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        String formattedDate = reservation.getReservationDate().format(formatter);

        String dayOfWeek = dateTime.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);

        String amPm = dateTime.getHour() < 12 ? "오전" : "오후";

        return new ReservationPaymentResponseDTO(reservationId,
                reservation.getUser().getId(),
                reservation.getRestaurant().getId(),
                reservation.getRestaurant().getName(),
                reservation.getRestaurant().getAddress().getRoadAddress(),
                reservation.getGuest(),
                formattedDate,
                reservation.getReservationTime(),
                reservation.getGuest() * 10000,
                dayOfWeek,
                amPm);
    }

    /**
     * 예약 등록
     */
    @Override
    public ReservationSaveResponseDTO saveReservation(ReservationRequestDTO reservationRequestDTO) {
        log.info("서비스 왔다리~");

        Restaurant restaurant = restaurantRepository.findById(reservationRequestDTO.getRestaurantId()).orElseThrow(() -> new IllegalArgumentException("식당이 없습니다."));

        User user = userRepository.findById(1L).orElseThrow(() -> new IllegalArgumentException("유저가 없습니다."));

        Reservation reservation = new Reservation(null, user, restaurant, null, reservationRequestDTO.getSelectGuest(), reservationRequestDTO.getSelectDate(), reservationRequestDTO.getSelectTime(),  LocalDateTime.now(), null);

        Reservation reservationComplete = reservationRepository.save(reservation);

        return new ReservationSaveResponseDTO(
                reservationComplete.getId(),
                reservationComplete.getRestaurant().getIsPenalty(),
                reservationRepository.existsById(reservation.getId()));
    }


    /**
     * 예약하기 : 예약 날짜를 선택했을 때 예약 시간 버튼 생성
     */
    @Override
    public ReservationDateResponseDTO getReservationTime(ReservationRequestDTO reservationRequestDTO) {
        log.info("안녕 난 서비스");

        log.info("잘 들어왔니? : " + reservationRequestDTO.getRestaurantId());

        List<Reservation> reservations = reservationRepository.findAllByRestaurantId(reservationRequestDTO.getRestaurantId());

        List<LocalTime> filteredTime;

        int guest = 0;

        if(reservationRequestDTO.getSelectTime() == null) {
            filteredTime = Timefilter(reservations, reservationRequestDTO);
            return new ReservationDateResponseDTO(reservationRequestDTO.getRestaurantId(),null, reservationRequestDTO.getSelectDate(),filteredTime);
        }
        else {
            guest = calRemainingGuest(reservations, reservationRequestDTO);
            return new ReservationDateResponseDTO(reservationRequestDTO.getRestaurantId(), guest, reservationRequestDTO.getSelectDate(),null);
        }
    }

    /**
     * 선택한 날짜 및 시간의 예약 인원 수 확인
     */
    public int calRemainingGuest(List<Reservation> reservations, ReservationRequestDTO reservationRequestDTO) {
        int totalReservationGuest = reservations.stream().map(Reservation::getRestaurant).distinct()
                .flatMap(reservation -> reservation.getSeats().stream())
                .mapToInt(seat -> seat.getSeatCount() * seat.getSeatCapacity())
                .sum();

        int calGuest = 0;

        LocalDate reservationDate = reservationRequestDTO.getSelectDate();
        log.info(reservationDate);
        LocalTime reservationTime = reservationRequestDTO.getSelectTime();
        log.info(reservationTime);

        Map<Integer, Integer> tables = null;

        for(Reservation reservation : reservations){
            if (tables == null){
                tables = new HashMap<>();

                for(Seat seat : reservation.getRestaurant().getSeats())
                    tables.put(seat.getSeatCapacity(), seat.getSeatCount());
            }

            log.info("시작!!!!!!!!!!!!!!!!!");

            //달력에서 선택한 예약 날짜의 인원 수를 계산하는 로직
            if(reservationDate.equals(reservation.getReservationDate())
                    && reservationTime == null){
                log.info("이곳은 날짜만 눌렀을 때다!");

                log.info("테이블 : " + tables.toString());

                //달력에서 선택한 날짜와 시간의 예약 하나 인원 수
                int guest = reservation.getGuest();
                log.info("인원 : " + guest);

                //몇 인용 테이블인지 우선 저장
                int[] seatCapacities = tables.keySet().stream().mapToInt(Integer::intValue).toArray();
                log.info(seatCapacities);

                for (int capacity : seatCapacities) {
                    while (guest > 0 && tables.containsKey(capacity) && tables.get(capacity) > 0) {
                        if (guest >= capacity) {
                            int tablesToAllocate = guest / capacity;

                            if (guest % capacity != 0)
                                tablesToAllocate++;

                            int availableTables = tables.get(capacity);
                            if (availableTables >= tablesToAllocate) {
                                tables.put(capacity, availableTables - tablesToAllocate);
                                calGuest += capacity * tablesToAllocate;
                                guest -= capacity * tablesToAllocate;
                            } else {
                                tables.put(capacity, 0);
                                calGuest += capacity * availableTables;
                                guest -= capacity * availableTables;
                            }
                        } else {
                            tables.put(capacity, tables.get(capacity) - 1);
                            guest = capacity;
                            calGuest += guest;
                            guest = 0;
                        }
                    }

                    // 남은 인원이 없다면 종료
                    if (guest == 0) {
                        break;
                    }
                }

                log.info("배정된 인원 수: " + calGuest);
            }

            else if(reservationDate.equals(reservation.getReservationDate())
                && reservationTime.equals(reservation.getReservationTime())
                && reservationTime != null){
                log.info("이곳은 날짜와 시간 모두 눌렀을 때다!");
                //테이블로 인원 수 계산하는 부분
                log.info("테이블 : " + tables.toString());

                //달력에서 선택한 날짜와 시간의 예약 하나 인원 수
                int guest = reservation.getGuest();
                log.info("인원 : " + guest);

                //몇 인용 테이블인지 우선 저장
                int[] seatCapacities = tables.keySet().stream().mapToInt(Integer::intValue).toArray();
                log.info(seatCapacities);


                for (int capacity : seatCapacities) {
                    while (guest > 0 && tables.containsKey(capacity) && tables.get(capacity) > 0) {
                        if (guest >= capacity) {
                            // 배정할 테이블 개수 계산
                            int tablesToAllocate = guest / capacity;
                            // 남은 인원이 있으면 한 테이블 더 배정
                            if (guest % capacity != 0) {
                                tablesToAllocate++;
                            }

                            // 필요한 테이블이 충분한지 확인
                            int availableTables = tables.get(capacity);
                            if (availableTables >= tablesToAllocate) {
                                tables.put(capacity, availableTables - tablesToAllocate);
                                calGuest += capacity * tablesToAllocate; // 배정된 테이블 수 만큼 인원 추가
                                guest -= capacity * tablesToAllocate;   // 배정된 인원 수만큼 차감
                            } else {
                                // 필요한 테이블이 부족한 경우, 가능한 만큼 배정
                                tables.put(capacity, 0);
                                calGuest += capacity * availableTables;  // 가능한 테이블만큼 배정
                                guest -= capacity * availableTables;     // 배정된 인원 차감
                            }
                        } else {
                            // 예약 신청 인원 수가 테이블 용량보다 적을 때, 해당 테이블 배정
                            tables.put(capacity, tables.get(capacity) - 1);
                            guest = capacity;
                            calGuest += guest;  // 남은 모든 인원을 배정
                            guest = 0;  // 모든 인원 배정 완료
                        }
                    }

                    // 남은 인원이 없다면 종료
                    if (guest == 0)
                        break;
                }

                log.info("배정된 인원 수: " + calGuest);
            }
        }

        log.info("예약 인원 수 :" + calGuest);

        return totalReservationGuest - calGuest;
    }

    /**
     * 예약 가능한 시간 필터링
     */
    public List<LocalTime> Timefilter(List<Reservation> reservations, ReservationRequestDTO reservationRequestDTO){
        List<LocalTime> reservationTimes = new ArrayList<>();

        LocalTime startTime = LocalTime.of(0, 0);

        int intervalMinutes = reservations.get(0).getRestaurant().getReservationTimeGap();
        log.info("갭 : " + intervalMinutes);

        do {
            reservationTimes.add(startTime);
            startTime = startTime.plusMinutes(intervalMinutes);
        } while (!startTime.equals(LocalTime.of(0, 0)));

        LocalDate curDate = reservationRequestDTO.getSelectDate();

        //내가 선택한 날짜의 요일
        String dayOfWeek = curDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN);

        Iterator<LocalTime> iterator = reservationTimes.iterator();
        while (iterator.hasNext()) {
            LocalTime localTime = iterator.next();
            log.info("가지고 온 것 : " + localTime);
            for (int i = 0; i < reservations.get(0).getRestaurant().getBusinessDays().size(); i++) {
                if (dayOfWeek.equals(reservations.get(0).getRestaurant().getBusinessDays().get(i).getDayOfWeek())) {
                    LocalTime openTime = reservations.get(0).getRestaurant().getBusinessDays().get(i).getOpenTime();
                    log.info("오픈 시간 : " + openTime);
                    LocalTime breakStartTime = reservations.get(0).getRestaurant().getBusinessDays().get(i).getBreakStartTime();
                    log.info("브레이스 시작 시간 : " + breakStartTime);
                    LocalTime breakEndTime = reservations.get(0).getRestaurant().getBusinessDays().get(i).getBreakEndTime();
                    log.info("브레이스 종료 시간 : " + breakEndTime);
                    LocalTime lastOrderTime = reservations.get(0).getRestaurant().getBusinessDays().get(i).getLastOrderTime();
                    log.info("라스트 오더 시간 : " + lastOrderTime);
                    boolean isOpen = reservations.get(0).getRestaurant().getBusinessDays().get(i).getRestaurant().getBusinessDays().get(i).getIsClose();
                    log.info("오픈 여부 : " + isOpen);

                    if(isOpen)
                        iterator.remove();
                    else{
                        if (openTime.isAfter(localTime) || lastOrderTime.isBefore(localTime)) {
                            log.info("잘가랑~ : " + localTime);
                            iterator.remove();
                        }

                        else if (!localTime.isBefore(breakStartTime) && localTime.isBefore(breakEndTime)) {
                            log.info("잘가랑~ : " + localTime);
                            iterator.remove();
                        }
                    }

                    int calRemainingGuest = calRemainingGuest(reservations, reservationRequestDTO);
                    log.info("예약 가능 인원 수 : " + calRemainingGuest);
                }
            }
        }

        return reservationTimes;
    }
}
