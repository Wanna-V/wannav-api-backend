package com.ssg.wannavapibackend.config;

import com.ssg.wannavapibackend.security.filter.JWTCheckFilter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@Log4j2
public class SecurityConfig {

    private JWTCheckFilter jwtCheckFilter;

    // JWTCheckFilter 의존성을 주입하기 위한 메서드
    @Autowired
    private void setJwtCheckFilter(JWTCheckFilter jwtCheckFilter) {
        this.jwtCheckFilter = jwtCheckFilter;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        log.info("filter chain............"); // 필터 체인이 설정될 때 로그 출력

        // 기본 로그인 폼 비활성화 (Spring Security가 제공하는 기본 로그인 페이지를 사용하지 않음)
        httpSecurity.formLogin(AbstractHttpConfigurer::disable);

        // 기본 로그아웃 기능 비활성화 (로그아웃 처리를 직접 구현)
        httpSecurity.logout(AbstractHttpConfigurer::disable);

        // CSRF 보호 비활성화 (토큰 기반 인증 사용 시 일반적으로 비활성화)
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        // 세션 정책 설정 (토큰 인증에서는 세션을 사용하지 않음)
        // Spring Security가 새로운 세션을 생성하지 않음
        httpSecurity.sessionManagement(sessionManagementConfigurer -> {
            sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.NEVER);
        });

        // JWTCheckFilter를 UsernamePasswordAuthenticationFilter 앞에 추가
        httpSecurity.addFilterBefore(jwtCheckFilter, UsernamePasswordAuthenticationFilter.class);

        // CORS 정책을 커스터마이징하기 위해 설정된 `corsConfigurationSource`를 사용
        // CORS 설정 추가
        httpSecurity.cors(cors -> {
            cors.configurationSource(corsConfigurationSource());
        });

        return httpSecurity.build(); // SecurityFilterChain 빈 반환
    }

    // 비밀번호 암호화 방식 설정 (BCryptPasswordEncoder 사용) -> 자체로그인 구현할 때 적용
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    // CORS 정책 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        // CORS 구성 생성
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // 모든 도메인(origin)을 허용 (모든 출처에서 요청 허용)
        corsConfiguration.setAllowedOriginPatterns(List.of("*"));

        // 허용되는 HTTP 메서드 설정
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS"));

        // 요청에서 허용되는 헤더 설정
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));

        // 자격 증명(Credentials) 허용 (쿠키나 인증 정보 포함 허용)
        corsConfiguration.setAllowCredentials(true);

        // URL 기반으로 CORS 정책 등록
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration); // 모든 경로에 대해 위 설정 적용

        return source;
    }
}
