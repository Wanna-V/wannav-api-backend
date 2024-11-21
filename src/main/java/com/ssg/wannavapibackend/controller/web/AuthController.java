package com.ssg.wannavapibackend.controller.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssg.wannavapibackend.dto.response.KakaoResponseDTO;
import com.ssg.wannavapibackend.security.util.JWTUtil;
import com.ssg.wannavapibackend.service.KakaoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final KakaoService kakaoService;

    private final JWTUtil jwtUtil;

    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("location", kakaoService.getKakaoLogin());
        return "/auth/login";
    }

    @GetMapping("/callback")
    public String callback(HttpServletRequest request) throws JsonProcessingException {
        KakaoResponseDTO kakaoResponseDTO = kakaoService.getKakaoInfo(request.getParameter("code"));
        log.info("토큰 받았고");
        log.info("Get username" + kakaoResponseDTO.getNickName());
        log.info("Get email" + kakaoResponseDTO.getEmail());
        String jwtToken = jwtUtil.login(kakaoResponseDTO.getNickName(), kakaoResponseDTO.getEmail());
        log.info(jwtToken);
        return "/restaurant/reservation2";
    }
}