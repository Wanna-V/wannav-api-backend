package com.ssg.wannavapibackend.controller.web;

import com.ssg.wannavapibackend.dto.response.ProductResponseDTO;
import com.ssg.wannavapibackend.dto.response.ReservationPaymentResponseDTO;
import com.ssg.wannavapibackend.service.ProductService;
import com.ssg.wannavapibackend.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final ReservationService reservationService;

    @GetMapping("/payment/reservation/{reservationId}")
    public String reservationPayment(@PathVariable Long reservationId, Model model) {
        log.info("결제로왔다!");

        ReservationPaymentResponseDTO reservationPaymentResponseDTO = reservationService.getReservationPayment(reservationId);

        model.addAttribute("reservationPaymentResponseDTO", reservationPaymentResponseDTO);

        return "/payment/reservation";
    }

    @GetMapping("/success")
    public String reservationPaySuccess(
            @RequestParam(value = "orderId") String orderId,
            @RequestParam(value = "amount") Integer amount,
            @RequestParam(value = "paymentKey") String paymentKey) {

        log.info(orderId);
        log.info(amount);
        log.info(paymentKey);
        log.info("성공~");
        return "/payment/success";
    }

    @GetMapping("/fail")
    public String reservationPayFail(
            @RequestParam(value = "orderId") String orderId,
            @RequestParam(value = "amount") Integer amount,
            @RequestParam(value = "paymentKey") String paymentKey) {

        log.info("실패~");
        return "/payment/success";
    }
}
