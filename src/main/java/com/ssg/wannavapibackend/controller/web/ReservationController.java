package com.ssg.wannavapibackend.controller.web;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Log4j2
@Controller
@RequestMapping("/reservation")
public class ReservationController {
    @GetMapping("/")
    public String test() {
        return "promotion/events";
    }
}