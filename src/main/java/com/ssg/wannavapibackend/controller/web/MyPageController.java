package com.ssg.wannavapibackend.controller.web;

import com.ssg.wannavapibackend.dto.request.MyPageUpdateDTO;
import com.ssg.wannavapibackend.dto.request.MyReservationRequestDTO;
import com.ssg.wannavapibackend.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("my")
    public String getMyPage(Model model) {
        model.addAttribute("my", myPageService.findMyPage(1L));
        return "user/mypage";
    }

    @GetMapping("my/edit")
    public String getMyPageEdit(Model model) {
        model.addAttribute("myPageUpdateDTO", myPageService.findUserInfo(1L));
        model.addAttribute("userInfo", myPageService.findUserInfo(1L));
        return "user/mypage-edit";
    }

    @PostMapping("my/edit")
    public String postMyPageEdit(Model model, @ModelAttribute @Validated MyPageUpdateDTO myPageUpdateDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("myPageUpdateDTO", myPageUpdateDTO);
            model.addAttribute("userInfo", myPageService.findUserInfo(1L));
            printErrorLog(bindingResult);
            return "user/mypage-edit";
        }
        myPageService.updateMyPage(1L, myPageUpdateDTO);
        log.info("마이페이지 수정 완료");
        return "redirect:/my";
    }

    @GetMapping("reservations")
    public String getMyReservations(MyReservationRequestDTO myReservationRequestDTO, Model model) {
        model.addAttribute("myReservation", myPageService.findMyReservations(1L, myReservationRequestDTO));
        return "user/my-reservation";
    }

    @PostMapping("reservations/{id}")
    public String getMyReservationsDetails(@PathVariable Long id, Model model) {
        if (id == null) {
            log.info("예약 정보를 찾을 수 없습니다.");
            return "redirect:/reservations";
        }
        model.addAttribute("myReservation", myPageService.findMyReservation(id));
        return "user/my-reservation-details";
    }

    @GetMapping("likes")
    public String getMyLikes(Model model) {
        model.addAttribute("myLikes", myPageService.findMyLikes(1L));
        return "user/my-likes";
    }

    @GetMapping("orders")
    public String getMyOrders(Model model) {
        model.addAttribute("myOrders", myPageService.findMyOrders(1L));
        return "user/my-order";
    }

    @PostMapping("orders/{id}")
    public String getMyOrdersDetails(@PathVariable Long id, Model model) {
        if (id == null) {
            log.info("주문 정보를 찾을 수 없습니다.");
            return "redirect:/orders";
        }
        model.addAttribute("myOrders", myPageService.findMyOrdersDetails(id));
        return "user/my-order-details";
    }

    @GetMapping("points")
    public String getMyPoints(Model model) {
        model.addAttribute("myPoints", myPageService.findMyPoints(1L));
        model.addAttribute("sum", myPageService.findUserInfo(1L));
        return "user/my-point";
    }

    @GetMapping("coupons")
    public String getMyCoupons(Model model) {
        model.addAttribute("myCoupons", myPageService.findMyCoupons(1L));
        return "user/my-coupon";
    }

    @GetMapping("reviews")
    public String getMyReviews(Model model) {
        model.addAttribute("myReviews", myPageService.findMyReviews(1L));
        model.addAttribute("reviewSum", myPageService.findMyPage(1L).getReviewCount());
        return "user/my-review";
    }

    private static void printErrorLog(BindingResult result) {
        log.info("{}", "*".repeat(20));
        for (FieldError fieldError : result.getFieldErrors()) {
            log.error("{}: {}", fieldError.getField(), fieldError.getDefaultMessage());
        }
        log.info("{}", "*".repeat(20));
    }
}
