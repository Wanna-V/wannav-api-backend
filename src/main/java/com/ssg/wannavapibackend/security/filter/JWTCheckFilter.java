package com.ssg.wannavapibackend.security.filter;

import com.ssg.wannavapibackend.security.auth.CustomUserPrincipal;
import com.ssg.wannavapibackend.security.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Log4j2

public class JWTCheckFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil; // JWT 검증 및 생성 유틸리티 클래스 의존성 주입

    // 특정 경로를 필터링에서 제외할지를 결정하는 메서드
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        if (request.getServletPath().startsWith("/css/") || request.getServletPath().startsWith("/js/") ||
                request.getServletPath().startsWith("/images/") || request.getServletPath().startsWith("/assets/")) {
            return true; // 필터를 적용하지 않음
        }
        // 경로가 "/api/v1/token/"로 시작하면 필터를 적용하지 않음
        if (request.getServletPath().startsWith("/restaurant/"))
            return true;
        if (request.getServletPath().startsWith("/auth/"))
            return true;
        // 나머지 경로는 필터를 적용
        return false;
    }


    /**
     * 요청과 응답을 할 떄 중간에 껴서 필터를 적용하여 JWT 검증하는 부분
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("JWTCheckFilter doFilter............ ");
        log.info("requestURI: " + request.getRequestURI());

        String headerStr = request.getHeader("Authorization"); // 요청 헤더에서 Authorization 값을 가져옴
        log.info("headerStr: " + headerStr); // Authorization 헤더 값 로그

        // Access Token이 없거나 형식이 올바르지 않은 경우 예외 처리
        if (headerStr == null || !headerStr.startsWith("Bearer ")) {
            handleException(response, new Exception("ACCESS TOKEN NOT FOUND")); // 예외 처리
            return; // 요청 중단
        }

        // "Bearer " 이후의 토큰 문자열만 추출
        String accessToken = headerStr.substring(7);

        try {
            // JWT 유효성 검증 및 정보 추출
            java.util.Map<String, Object> tokenMap = jwtUtil.validateToken(accessToken);
            log.info("tokenMap: " + tokenMap); // 토큰 검증 결과 로그

            // 토큰에서 사용자 ID 추출
            String mid = tokenMap.get("mid").toString();

            // 역할 정보(role)를 쉼표로 나누어 배열로 변환
            String[] roles = tokenMap.get("role").toString().split(",");

            // 사용자 인증 객체 생성
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            new CustomUserPrincipal(mid), // 사용자 정보를 CustomUserPrincipal로 래핑
                            null, // 자격 증명은 null로 설정
                            Arrays.stream(roles) // 역할 정보를 스트림으로 처리
                                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // "ROLE_" 접두사 추가
                                    .collect(Collectors.toList()) // 권한 목록으로 변환
                    );

            // SecurityContext에 인증 객체 저장
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authenticationToken);

            // 다음 필터 실행
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // JWT 검증 중 예외 발생 시 처리
            handleException(response, e);
        }
    }

    // 예외가 발생했을 때 클라이언트에 에러 응답을 보내는 메서드
    private void handleException(HttpServletResponse response, Exception e) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 응답 상태를 403(금지)로 설정
        response.setContentType("application/json"); // 응답 타입을 JSON으로 설정
        response.getWriter().println("{\"error\": \"" + e.getMessage() + "\"}"); // 에러 메시지 JSON 형식으로 반환
    }
}
