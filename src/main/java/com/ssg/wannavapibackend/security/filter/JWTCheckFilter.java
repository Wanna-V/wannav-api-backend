//package com.ssg.wannavapibackend.security.filter;
//
//import com.ssg.wannavapibackend.security.auth.CustomUserPrincipal;
//import com.ssg.wannavapibackend.security.util.JWTUtil;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Component
//@RequiredArgsConstructor
//@Log4j2
//public class JWTCheckFilter extends OncePerRequestFilter {
//
//    private final JWTUtil jwtUtil;
//
//    /**
//     * 필터를 적용하지 않는 부분
//     *
//     * @param request
//     */
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        if (request.getServletPath().startsWith("/css/") || request.getServletPath().startsWith("/js/") ||
//                request.getServletPath().startsWith("/images/") || request.getServletPath().startsWith("/assets/"))
//            return true;
//        if (request.getRequestURI().equals("/restaurant/"))
//            return true;
//        if (request.getServletPath().startsWith("/auth/"))
//            return true;
//
//        return false;
//    }
//
//
//    /**
//     * 요청과 응답을 할 떄 중간에 껴서 필터를 적용하여 JWT 검증하는 부분
//     *
//     * @param request
//     * @param response
//     * @param filterChain
//     * @throws ServletException
//     * @throws IOException
//     */
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        if (shouldNotFilter(request)) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        try {
//            log.info(jwtUtil.getRefreshTokenCookie(request));
//
//            if(jwtUtil.getRefreshTokenCookie(request) == null) {
//                throw new BadCredentialsException("로그인이 필요합니다.");
//            }
//
//            Map<String, Object> tokenMap = jwtUtil.validateToken(jwtUtil.getAccessTokenCookie(request), request, response);
//            log.info("tokenMap: " + tokenMap);
//
//            String mid = tokenMap.get("id").toString();
//
//            List<GrantedAuthority> authorities = Collections.emptyList();
//
//            if (tokenMap.containsKey("role") && tokenMap.get("role") != null) {
//                String[] roles = tokenMap.get("role").toString().split(",");
//                authorities = Arrays.stream(roles)
//                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
//                        .collect(Collectors.toList());
//            }
//
//            UsernamePasswordAuthenticationToken authenticationToken =
//                    new UsernamePasswordAuthenticationToken(
//                            new CustomUserPrincipal(mid),
//                            null,
//                            authorities
//                    );
//
//            SecurityContext context = SecurityContextHolder.getContext();
//            context.setAuthentication(authenticationToken);
//
//            filterChain.doFilter(request, response);
//
//        } catch (Exception e){
//            handleException(response, e);
//        }
//    }
//
//    private void handleException(HttpServletResponse response, Exception e) throws IOException {
//        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//        response.setContentType("application/json");
//        response.getWriter().println("{\"error\": \"" + e.getMessage() + "\"}");
//        response.sendRedirect("/auth/login");
//    }
//}
