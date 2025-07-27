package com.example.hackerthon.Login.controller;

import com.example.hackerthon.Login.dto.*;
import com.example.hackerthon.Login.password.ChangePasswordDto;
import com.example.hackerthon.Login.password.PasswordResetChangeDto;
import com.example.hackerthon.Login.password.PasswordResetRequestDto;
import com.example.hackerthon.Login.password.PasswordResetVerifyDto;
import com.example.hackerthon.Login.service.AuthService;
import com.example.hackerthon.community.post.dto.PostResponse;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequestDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String msg = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            return ResponseEntity.badRequest().body(msg);
        }
        authService.signup(dto);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
        AuthResponseDto res = authService.login(dto);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("토큰 없음");
        }
        String token = authHeader.substring(7);
        authService.logout(token);
        return ResponseEntity.ok("로그아웃 성공");
    }

    // ✅ 리프레시 토큰으로 access/refresh 재발급
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refreshToken(@RequestHeader("Authorization") String refreshTokenHeader) {
        if (refreshTokenHeader == null || !refreshTokenHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }
        String refreshToken = refreshTokenHeader.substring(7);
        AuthResponseDto res = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/password-reset/request")
    public ResponseEntity<String> requestPasswordReset(@RequestBody PasswordResetRequestDto dto) throws MessagingException {
        authService.requestPasswordReset(dto);
        return ResponseEntity.ok("비밀번호 재설정 인증번호를 이메일로 발송했습니다.");
    }

    @PostMapping("/password-reset/verify")
    public ResponseEntity<String> verifyPasswordResetCode(@RequestBody PasswordResetVerifyDto dto) {
        authService.verifyPasswordResetCode(dto);
        return ResponseEntity.ok("인증번호가 유효합니다.");
    }

    @PostMapping("/password-reset/change")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetChangeDto dto) {
        authService.resetPassword(dto);
        return ResponseEntity.ok("비밀번호가 변경되었습니다.");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordDto dto,
            Authentication authentication
    ) {
        String username = authentication.getName();
        authService.changePassword(username, dto);
        return ResponseEntity.ok("비밀번호가 변경되었습니다.");
    }

    @PostMapping("/profile-image")
    public ResponseEntity<String> updateProfileImage(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) throws Exception {
        String username = authentication.getName();
        authService.updateProfileImage(username, file);
        return ResponseEntity.ok("프로필 사진이 변경되었습니다.");
    }

    @GetMapping("/me")
    public ResponseEntity<MyInfoResponseDto> getMyInfo(Authentication authentication) {
        String username = authentication.getName();
        MyInfoResponseDto dto = authService.getMyInfo(username);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/update-nickname")
    public ResponseEntity<String> updateNickname(
            @RequestBody UpdateNicknameDto dto,
            Authentication authentication
    ) {
        String username = authentication.getName();
        authService.updateNickname(username, dto);
        return ResponseEntity.ok("닉네임이 변경되었습니다.");
    }

    @GetMapping("/nickname-check")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname) {
        boolean isDuplicate = authService.isNicknameDuplicate(nickname);
        return ResponseEntity.ok(isDuplicate);
    }

    @DeleteMapping("/profile-image")
    public ResponseEntity<String> deleteProfileImage(Authentication authentication) {
        String username = authentication.getName();
        authService.deleteProfileImage(username);
        return ResponseEntity.ok("프로필 사진이 기본 이미지로 변경되었습니다.");
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<String> withdraw(
            Authentication authentication,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        String username = authentication.getName();
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        authService.withdraw(username, token);
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }

    @GetMapping("/my-comments/posts")
    public ResponseEntity<List<PostResponse>> getPostsICommentedOn(Authentication authentication) {
        String username = authentication.getName();
        List<PostResponse> result = authService.getPostsCommentedBy(username);
        return ResponseEntity.ok(result);
    }
}
