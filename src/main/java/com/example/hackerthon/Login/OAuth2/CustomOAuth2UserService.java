package com.example.hackerthon.Login.OAuth2;

import com.example.hackerthon.Login.entity.User;
import com.example.hackerthon.Login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    @SuppressWarnings("unchecked")
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1) 기본 서비스로 OAuth2User 정보 받아오기
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 2) 소셜 구분
        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "naver", "google", "kakao"
        Map<String, Object> attributes = oAuth2User.getAttributes();

        System.out.println("attributes = " + attributes);

        final String email;
        String nicknameForSave;
        final String pictureUrl;
        final String provider = registrationId.toUpperCase();

        Map<String, Object> principalAttributes;
        String nameAttributeKey = "email";

        switch (registrationId) {
            case "naver" -> {
                Map<String, Object> resp = (Map<String, Object>) attributes.get("response");
                email = resp != null ? (String) resp.get("email") : null;
                nicknameForSave = resp != null ? (String) resp.get("name") : null;
                pictureUrl = resp != null ? (String) resp.get("profile_image") : null;

                principalAttributes = resp; // ★ 핵심: response 맵을 principal로!
                // nameAttributeKey는 그대로 "email"
            }
            case "google" -> {
                email = (String) attributes.get("email");
                nicknameForSave = (String) attributes.get("name");
                pictureUrl = (String) attributes.get("picture");

                principalAttributes = attributes;
                // nameAttributeKey는 그대로 "email"
            }
            case "kakao" -> {
                Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
                email = account != null ? (String) account.get("email") : null;
                Map<String, Object> profile = account != null ? (Map<String, Object>) account.get("profile") : null;
                nicknameForSave = profile != null ? (String) profile.get("nickname") : null;
                pictureUrl = profile != null ? (String) profile.get("profile_image_url") : null;

                principalAttributes = account; // ★ 핵심: kakao_account 맵을 principal로!
                // nameAttributeKey는 그대로 "email"
            }
            default -> throw new OAuth2AuthenticationException("Unsupported provider: " + registrationId);
        }

        // ★★ email 필수 체크!
        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException("소셜 프로필에서 email을 받아오지 못했습니다. (필수 정보 누락)");
        }
        if (nicknameForSave == null || nicknameForSave.isBlank()) {
            nicknameForSave = email;
        }

        // 4) DB에서 조회, 없으면 회원가입
        final String finalNickname = nicknameForSave;
        userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .username(email)
                                .email(email)
                                .nickname(finalNickname)
                                .password("SOCIAL_LOGIN_" + provider)
                                .provider(provider)
                                .role(User.Role.USER)
                                .profileImageUrl(pictureUrl)
                                .build()
                ));

        // 5) 인증된 OAuth2User 반환
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                principalAttributes,
                nameAttributeKey
        );
    }
}
