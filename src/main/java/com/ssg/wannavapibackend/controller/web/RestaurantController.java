package com.ssg.wannavapibackend.controller.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/restaurant")
public class RestaurantController {

    @GetMapping("/")
    public String test1(Model model) {
        model.addAttribute("restaurantId",1);
        return "restaurant/reservation2";
    }
}
