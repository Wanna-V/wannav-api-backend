package com.ssg.wannavapibackend.service.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.wannavapibackend.config.KakaoConfig;
import com.ssg.wannavapibackend.domain.User;
import com.ssg.wannavapibackend.dto.response.KakaoResponseDTO;
import com.ssg.wannavapibackend.repository.UserRepository;
import com.ssg.wannavapibackend.service.KakaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Log4j2
public class KakaoServiceImpl implements KakaoService {

    private final UserRepository userRepository;
    private final KakaoConfig kakaoConfig;

    // 카카오 로그인 URL 반환
    public String getKakaoLogin() {
        return kakaoConfig.getAuthUri() + "/oauth/authorize"
                + "?client_id=" + kakaoConfig.getClientId()
                + "&redirect_uri=" + kakaoConfig.getRedirectUri()
                + "&response_type=code";
    }

    // 카카오에서 사용자 정보 받아오는 메서드
    public KakaoResponseDTO getKakaoInfo(String code) throws JsonProcessingException {
        String accessToken = getAccessToken(code);
        return this.getUserInfoFromToken(accessToken);
    }


    // 인증 코드로 액세스 토큰을 발급받는 메서드
    @Override
    public String getAccessToken(String code) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoConfig.getClientId());
        params.add("client_secret", kakaoConfig.getClientSecret());
        params.add("code", code);
        params.add("redirect_uri", kakaoConfig.getRedirectUri());

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                kakaoConfig.getAuthUri() + "/oauth/token",
                HttpMethod.POST,
                httpEntity,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseBody = objectMapper.readTree(response.getBody());
        return responseBody.get("access_token").asText();
    }

    // 액세스 토큰으로 카카오 사용자 정보 가져오는 메서드
    public KakaoResponseDTO getUserInfoFromToken(String accessToken) throws JsonProcessingException {
        log.info("엑세스 토큰: {}", accessToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                kakaoConfig.getApiUri() + "/v2/user/me",
                HttpMethod.GET,
                httpEntity,
                String.class
        );
        log.info("응답: {}", response);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseBody = objectMapper.readTree(response.getBody());

        JsonNode kakaoAccount = responseBody.get("kakao_account");
        JsonNode profile = kakaoAccount.get("profile");

        String profileImage = profile.has("profile_image_url") ? profile.get("profile_image_url").asText() : "";
        String email = kakaoAccount.get("email").asText();
        String nickName = profile.get("nickname").asText();

        User user = userRepository.findUserByEmail(email);

        if(user == null){
            user = new User(null, nickName, profileImage, email, null, null, null, uniqueCode(), 0L, true, false, LocalDateTime.now(), null, null);
            User userInfo = userRepository.save(user);
            return new KakaoResponseDTO(userInfo.getId(), userInfo.getEmail(), userInfo.getUsername());
        }

        else
            return new KakaoResponseDTO(user.getId(), user.getEmail(), user.getUsername());
    }

    private String uniqueCode() {
        Random random = new Random();
        String code;
        do {
            int randomNumber = 100000 + random.nextInt(900000);
            code = String.valueOf(randomNumber);
        } while (userRepository.existsByCode(code));
        return code;
    }
}
