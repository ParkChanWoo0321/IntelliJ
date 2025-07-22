package com.example.hackerthon.Login.jwt;

import com.example.hackerthon.Login.repository.UserRepository;
import com.example.hackerthon.Login.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final TokenBlacklistService tokenBlacklistService;

    // 인증 없이 접근 허용할 API 엔드포인트 (정확히 일치하는 URI만!)
    private static final List<String> AUTH_WHITELIST = Arrays.asList(
            "/api/auth/signup",
            "/api/auth/login",
            "/api/auth/password-reset/request",
            "/api/auth/password-reset/verify",
            "/api/auth/password-reset/change",
            "/oauth2/authorization/naver",
            "/oauth2/authorization/kakao",
            "/oauth2/authorization/google",
            "/login/oauth2/code/naver",
            "/login/oauth2/code/kakao",
            "/login/oauth2/code/google",
            "/error",
            "/ws-notify"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        System.out.println("[JwtAuthFilter] called! path=" + path);

        // ★ 인증 없이 허용되는 경로만 통과!
        if (AUTH_WHITELIST.contains(path)) {
            System.out.println("[JwtAuthFilter] Skip filter for path: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        // JWT 인증 처리
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("[JwtAuthFilter] Bearer token found: " + token);

            if (tokenBlacklistService.isBlacklisted(token)) {
                System.out.println("[JwtAuthFilter] Token is blacklisted");
                throw new BadCredentialsException("이미 로그아웃된 토큰입니다.");
            }

            if (!jwtUtil.validateToken(token)) {
                System.out.println("[JwtAuthFilter] Token is invalid");
                throw new AuthenticationCredentialsNotFoundException("JWT 토큰이 유효하지 않음");
            }

            // username 으로 조회
            String username = jwtUtil.getUsernameFromToken(token);
            System.out.println("[JwtAuthFilter] username from token: " + username);

            UserDetails userDetails = userRepository.findByUsername(username)
                    .map(user -> org.springframework.security.core.userdetails.User
                            .withUsername(user.getUsername())
                            .password(user.getPassword())
                            .roles(user.getRole().name())
                            .build())
                    .orElse(null);

            if (userDetails != null) {
                var authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("[JwtAuthFilter] Authentication set for: " + username);
            } else {
                System.out.println("[JwtAuthFilter] User not found for username: " + username);
            }
        } else {
            System.out.println("[JwtAuthFilter] No Bearer token found");
        }

        filterChain.doFilter(request, response);
    }
}
