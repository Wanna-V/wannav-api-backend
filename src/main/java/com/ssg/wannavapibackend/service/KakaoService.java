package com.ssg.wannavapibackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssg.wannavapibackend.dto.response.KakaoResponseDTO;

public interface KakaoService {
    KakaoResponseDTO getUserInfoFromToken(String accessToken) throws JsonProcessingException;

    String getKakaoLogin();

    KakaoResponseDTO getKakaoInfo(String code) throws JsonProcessingException;

    String getAccessToken(String code) throws JsonProcessingException;
}
