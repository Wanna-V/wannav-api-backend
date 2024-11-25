package com.ssg.wannavapibackend.controller.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class ReservationController {

    @GetMapping("restaurant/{restaurantId}")
    public String test2(@PathVariable("restaurantId") Long restaurantId, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("restaurantId", restaurantId);

        return "redirect:/reservation/calendar";
    }

    @GetMapping("/reservation/calendar")
    public String reservation1(@ModelAttribute("restaurantId") Long restaurantId) {

        return "/reservation/calendar";
    }

    @GetMapping("/reservation")
    public String reservation() {

        return "/payment/success";
    }
}
