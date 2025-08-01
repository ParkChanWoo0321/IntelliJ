package com.example.hackerthon.Login.service;

import com.example.hackerthon.Login.dto.*;
import com.example.hackerthon.Login.entity.PasswordResetCode;
import com.example.hackerthon.Login.entity.User;
import com.example.hackerthon.Login.password.*;
import com.example.hackerthon.Login.password.PasswordResetCodeRepository;
import com.example.hackerthon.Login.repository.UserRepository;
import com.example.hackerthon.Login.jwt.JwtUtil;
import com.example.hackerthon.comment.entity.Comment;
import com.example.hackerthon.comment.repository.CommentRepository;
import com.example.hackerthon.community.Like.entity.Like;
import com.example.hackerthon.community.Like.repository.LikeRepository;
import com.example.hackerthon.community.Scrap.entity.Scrap;
import com.example.hackerthon.community.Scrap.repository.ScrapRepository;
import com.example.hackerthon.community.post.dto.PostMediaDto;
import com.example.hackerthon.community.post.dto.PostResponse;
import com.example.hackerthon.community.post.entity.Post;
import com.example.hackerthon.community.post.repository.PostRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final PasswordResetCodeRepository passwordResetCodeRepository;
    private final EmailService emailService;

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final ScrapRepository scrapRepository;
    private final CommentRepository commentRepository;

    public void signup(SignupRequestDto dto) {
        if (userRepository.existsByUsername(dto.getUsername()))
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        if (userRepository.existsByEmail(dto.getEmail()))
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        if (userRepository.existsByNickname(dto.getNickname()))
            throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");

        if (!PasswordValidator.isValid(dto.getPassword())) {
            throw new IllegalArgumentException("비밀번호는 8자 이상, 영문/숫자/특수문자를 각각 1개 이상 포함해야 합니다.");
        }

        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .nickname(dto.getNickname())
                .provider("LOCAL")
                .role(User.Role.USER)
                .build();

        userRepository.save(user);
    }

    public AuthResponseDto login(LoginRequestDto dto) {
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword()))
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");

        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        return new AuthResponseDto(accessToken, refreshToken, user.getUsername(), user.getRole().name());
    }

    public void logout(String token) {
        tokenBlacklistService.addToBlacklist(token);
    }

    // ⭐️ 로그인 유지(리프레시 토큰으로 Access/Refresh 재발급)
    public AuthResponseDto refreshToken(String refreshToken) {
        // 블랙리스트 체크
        if (tokenBlacklistService.isBlacklisted(refreshToken)) {
            throw new IllegalArgumentException("블랙리스트에 등록된 리프레시 토큰입니다.");
        }
        // 토큰 유효성 체크
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }
        // username 추출
        String username = jwtUtil.getUsernameFromToken(refreshToken);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        String newAccessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRole().name());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        return new AuthResponseDto(newAccessToken, newRefreshToken, user.getUsername(), user.getRole().name());
    }

    // ===== 아래 기존 코드 동일 =====

    public void updateProfileImage(String username, MultipartFile file) throws IOException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        String uploadDir = "profile_uploads/";
        File dir = new File(uploadDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("프로필 이미지 폴더 생성 실패");
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File dest = new File(uploadDir + fileName);
        file.transferTo(dest);

        String imageUrl = "/" + uploadDir + fileName;
        user.setProfileImageUrl(imageUrl);

        userRepository.save(user);
    }

    @Transactional
    public void requestPasswordReset(PasswordResetRequestDto dto) throws MessagingException {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일입니다."));

        passwordResetCodeRepository.deleteByEmail(user.getEmail());

        String code = String.format("%06d", new Random().nextInt(1_000_000));
        PasswordResetCode resetCode = PasswordResetCode.builder()
                .email(user.getEmail())
                .code(code)
                .expiryDate(LocalDateTime.now().plusMinutes(10))
                .build();

        passwordResetCodeRepository.save(resetCode);
        emailService.sendResetCode(user.getEmail(), code);
    }

    @Transactional
    public void verifyPasswordResetCode(PasswordResetVerifyDto dto) {
        PasswordResetCode codeEntity = passwordResetCodeRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("인증 요청 내역이 없습니다."));

        if (!codeEntity.getCode().equals(dto.getCode())) {
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }
        if (codeEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("인증번호가 만료되었습니다.");
        }
    }

    @Transactional
    public void resetPassword(PasswordResetChangeDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일입니다."));

        if (!PasswordValidator.isValid(dto.getNewPassword())) {
            throw new IllegalArgumentException("비밀번호는 8자 이상, 영문/숫자/특수문자를 각각 1개 이상 포함해야 합니다.");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        passwordResetCodeRepository.deleteByEmail(dto.getEmail());
    }

    @Transactional
    public void changePassword(String username, ChangePasswordDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        if (!PasswordValidator.isValid(dto.getNewPassword())) {
            throw new IllegalArgumentException("비밀번호는 8자 이상, 영문/숫자/특수문자를 각각 1개 이상 포함해야 합니다.");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    public MyInfoResponseDto getMyInfo(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        List<Post> myPosts = postRepository.findByAuthor_Username(username);
        List<Post> myScraps = scrapRepository.findByUser(user).stream().map(Scrap::getPost).toList();
        List<Post> myLikes = likeRepository.findByUser(user).stream().map(Like::getPost).toList();

        List<Comment> myComments = commentRepository.findByAuthor(user);
        Set<Post> commentedPostsSet = myComments.stream()
                .map(Comment::getPost)
                .collect(Collectors.toSet());
        List<PostResponse> myCommentedPosts = commentedPostsSet.stream()
                .map(this::toPostResponse)
                .toList();

        MyInfoResponseDto dto = new MyInfoResponseDto();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setNickname(user.getNickname());
        dto.setRole(user.getRole().name());
        dto.setProfileImageUrl(user.getProfileImageUrl());
        dto.setMyPosts(myPosts.stream().map(this::toPostResponse).toList());
        dto.setMyScraps(myScraps.stream().map(this::toPostResponse).toList());
        dto.setMyLikes(myLikes.stream().map(this::toPostResponse).toList());
        dto.setMyCommentedPosts(myCommentedPosts);

        return dto;
    }

    private PostResponse toPostResponse(Post post) {
        int likeCount = likeRepository.countByPost(post);
        int scrapCount = scrapRepository.countByPost(post);
        int commentCount = commentRepository.countByPost(post);

        PostResponse resp = new PostResponse(post, likeCount, scrapCount, commentCount);

        if (post.getMediaList() != null) {
            resp.setMediaList(
                    post.getMediaList().stream()
                            .map(media -> {
                                PostMediaDto dto = new PostMediaDto();
                                dto.setUrl(media.getUrl());
                                dto.setType(media.getType());
                                return dto;
                            })
                            .collect(Collectors.toList())
            );
        }

        return resp;
    }

    public void updateNickname(String username, UpdateNicknameDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));
        if (userRepository.existsByNickname(dto.getNickname())) {
            throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
        }
        user.setNickname(dto.getNickname());
        userRepository.save(user);
    }

    public boolean isNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public void deleteProfileImage(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));
        user.setProfileImageUrl("/profile_uploads/default_profile.png");
        userRepository.save(user);
    }

    @Transactional
    public void withdraw(String username, String token) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        userRepository.delete(user);

        if (token != null) {
            tokenBlacklistService.addToBlacklist(token);
        }
    }

    public List<PostResponse> getPostsCommentedBy(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저"));

        List<Comment> comments = commentRepository.findByAuthor(user);
        Set<Post> uniquePosts = comments.stream()
                .map(Comment::getPost)
                .collect(Collectors.toSet());

        return uniquePosts.stream()
                .map(post -> new PostResponse(
                        post,
                        likeRepository.countByPost(post),
                        scrapRepository.countByPost(post),
                        commentRepository.countByPost(post)
                ))
                .toList();
    }
}
