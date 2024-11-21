package com.ssg.wannavapibackend.security.util;

import com.ssg.wannavapibackend.domain.User;
import com.ssg.wannavapibackend.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
@RequiredArgsConstructor
public class JWTUtil {

    private static final String key = "1234567890123456789012345678901234567890";

    private final UserRepository userRepository;
    /**
     * JWT 토큰 생성 메서드
     *
     * @param valueMap 토큰에 포함할 클레임 정보 (예: 이메일, 권한 등)
     * @param min      토큰의 만료 시간 (분 단위)
     * @return 생성된 JWT 토큰 문자열
     */
    public String createToken(Map<String, Object> valueMap, int min) {
        SecretKey key = null;

        try {
            // **암호화 키 생성**: `key` 문자열을 HMAC-SHA256 알고리즘에 맞게 SecretKey로 변환
            key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            // 키 변환 실패 시 예외 처리
            throw new RuntimeException(e.getMessage());
        }

        // **JWT 토큰 빌더**
        return Jwts.builder()
                .header() // 헤더 설정 시작
                .add("typ", "JWT") // JWT 타입 명시
                .add("alg", "HS256") // 서명 알고리즘 명시
                .and() // 헤더 설정 종료
                .issuedAt(Date.from(ZonedDateTime.now().toInstant())) // 발행 시간 설정
                .expiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant())) // 만료 시간 설정
                .claims(valueMap) // 클레임 추가 (사용자 정보)
                .signWith(key) // 키를 사용하여 서명
                .compact(); // 토큰 생성 및 문자열 반환
    }

    /**
     * JWT 토큰 검증 메서드
     *
     * @param token 클라이언트로부터 받은 JWT 토큰
     * @return 클레임 정보 (JWT에 포함된 사용자 정보와 메타데이터)
     */
    public Map<String, Object> validateToken(String token) {
        SecretKey key = null;
        try {
            // **암호화 키 생성**: 토큰 검증을 위해 동일한 키로 SecretKey 생성
            key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            // 키 변환 실패 시 예외 처리
            throw new RuntimeException(e.getMessage());
        }

        // **JWT 검증 및 클레임 파싱**
        Claims claims = Jwts.parser() // JWT 파서 생성
                .verifyWith(key) // 서명 검증을 위해 SecretKey 지정
                .build() // 파서 빌드
                .parseSignedClaims(token) // 토큰을 파싱하여 클레임 획득
                .getPayload(); // 클레임 내용 반환

        log.info("claims: " + claims); // 검증된 클레임을 로그로 출력

        // 클레임 정보를 반환 (예: 이메일, 권한 등)
        return claims;
    }

    public String login(String userName, String email) {
        // 사용자 인증 로직 (예: 사용자 존재 여부 확인, 비밀번호 확인 등)
        log.info(userName);
        log.info(email);

        Long userId = userRepository.findIdByUsernameAndEmail(userName, email);

        log.info(userId);

        if (userId == null) {
            throw new RuntimeException("로그인해라");
        }

        // 사용자 인증 성공 시, JWT 생성
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId);

        // JWT 생성 (유효 기간 60분)
        return createToken(claims, 60);
    }
}