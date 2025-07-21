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
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // 여기 추가: 인증 예외 경로(permitAll)에서는 바로 다음 필터로 넘긴다
        if (path.startsWith("/api/auth")
                || path.startsWith("/api/oauth")
                || path.startsWith("/oauth2/authorization")
                || path.startsWith("/login/oauth2/code")
                || path.startsWith("/error")  // 에러핸들러 등
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        // 여기서부터는 원래 코드
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (tokenBlacklistService.isBlacklisted(token)) {
                throw new BadCredentialsException("이미 로그아웃된 토큰입니다.");
            }

            if (!jwtUtil.validateToken(token)) {
                throw new AuthenticationCredentialsNotFoundException("JWT 토큰이 유효하지 않음");
            }

            String email = jwtUtil.getUsernameFromToken(token);

            UserDetails userDetails = userRepository.findByEmail(email)
                    .map(user -> org.springframework.security.core.userdetails.User
                            .withUsername(user.getEmail())
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
            }
        }
        filterChain.doFilter(request, response);
    }
}