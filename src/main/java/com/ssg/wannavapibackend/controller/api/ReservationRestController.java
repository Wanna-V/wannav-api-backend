package com.ssg.wannavapibackend.controller.api;

import com.ssg.wannavapibackend.dto.request.ReservationRequestDTO;
import com.ssg.wannavapibackend.dto.response.ReservationSaveResponseDTO;
import com.ssg.wannavapibackend.dto.response.ReservationDateResponseDTO;
import com.ssg.wannavapibackend.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservation")
public class ReservationRestController {

    private final ReservationService reservationService;

    @GetMapping("/date")
    public ReservationDateResponseDTO getReservationTime(@ModelAttribute("reservationRequestDTO") ReservationRequestDTO reservationRequestDTO) {
        log.info("안녕 난 레스트컨트롤러");
        log.info("안녕 난 레스트컨트롤러" + reservationRequestDTO.getRestaurantId());
        log.info("안녕 난 레스트컨트롤러" + reservationRequestDTO.getSelectDate());
        log.info("안녕 난 레스트컨트롤러" + reservationRequestDTO.getSelectTime());

        return reservationService.getReservationTime(reservationRequestDTO);
    }

    @GetMapping("/time")
    public ReservationDateResponseDTO getReservationGuest(@ModelAttribute("reservationRequestDTO") ReservationRequestDTO reservationRequestDTO) {
        log.info("안녕 난 레스트컨트롤러2");
        log.info("안녕 난 레스트컨트롤러" + reservationRequestDTO.getRestaurantId());
        log.info("안녕 난 레스트컨트롤러" + reservationRequestDTO.getSelectDate());
        log.info("안녕 난 레스트컨트롤러" + reservationRequestDTO.getSelectTime());


        return reservationService.getReservationTime(reservationRequestDTO);
    }

    @PostMapping("/confirm")
    public ResponseEntity<Map<String, Object>> saveReservation(@ModelAttribute("reservationRequestDTO") ReservationRequestDTO reservationRequestDTO) {
        Map<String, Object> response = new HashMap<>();

        try {
            ReservationSaveResponseDTO reservation = reservationService.saveReservation(reservationRequestDTO);

            log.info("여기도 왔어");

            if (!reservation.getIsPenalty() && reservation.getIsSave()) {
                response.put("message", "예약이 성공적으로 완료되었습니다.");
                response.put("status", "success");
            }

            else if (reservation.getIsPenalty() && reservation.getIsSave()) {
                response.put("reservation", reservation);
                response.put("status", "payment");
            }

            else {
                response.put("message", "예약에 실패했습니다. 다시 시도해주세요.");
                response.put("status", "error");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "예약 날짜, 예약 시간, 인원 수 모두 선택해야 합니다!");
            response.put("status", "error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}