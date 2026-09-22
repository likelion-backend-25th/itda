package net.likelion.bebc25.itda.security.oauth;

import lombok.extern.slf4j.Slf4j;
import net.likelion.bebc25.itda.domain.Member;
import net.likelion.bebc25.itda.member.mapper.MemberMapper;
import net.likelion.bebc25.itda.security.principal.CustomUserDetails;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberMapper memberMapper;

    public CustomOAuth2UserService(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 스프링 기본 구현체를 통해 소셜 UserInfo 엔드포인트에서 프로필 JSON 정보 조회
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 2. 현재 로그인 요청이 들어온 소셜 공급자 식별 ("google", "kakao")
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 3. 플랫폼별 응답 JSON 속성에서 회원 정보 추출 후 DB 자동 가입 또는 조회
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Member member = saveOrUpdate(registrationId, attributes);

        // 4. 도메인 회원 엔티티와 원시 attributes를 모두 보관하는 통합 인증 주체 반환
        return new CustomUserDetails(member, attributes);
    }

    // 소셜 플랫폼별 상이한 JSON 구조를 분석하여 Member 엔티티로 변환 및 DB 반영
    private Member saveOrUpdate(String registrationId, Map<String, Object> attributes) {
        String email;
        String nickname;

        switch (registrationId.toLowerCase()) {
            case "google" -> {
                email = (String) attributes.get("email");
                nickname = (String) attributes.get("name");
            }
            case "kakao" -> {
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                Map<String, Object> profile = (kakaoAccount != null) ? (Map<String, Object>) kakaoAccount.get("profile") : null;
                email = (kakaoAccount != null) ? (String) kakaoAccount.get("email") : null;
                nickname = (profile != null) ? (String) profile.get("nickname") : "KakaoUser_" + UUID.randomUUID().toString().substring(0, 6);

                // 카카오 비즈 앱 미전환 또는 사용자 동의 거부로 이메일이 null인 경우: 카카오 고유 id 기반 가상 이메일 생성
                if (email == null || email.isBlank()) {
                    Object kakaoId = attributes.get("id");
                    email = "kakao_" + kakaoId + "@kakao.social";
                    log.info("카카오 이메일 미제공 계정: 대체 가상 이메일 [{}] 생성", email);
                }
            }
            default -> throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인 공급자입니다: " + registrationId);
        }

        // DB에 해당 이메일의 기존 회원이 있는지 조회
        Member existingMember = memberMapper.findByEmail(email);
        if (existingMember == null) {
            // 최초 로그인인 경우 자동 회원가입 진행
            Member newMember = Member.builder()
                    .email(email)
                    .password("") // 소셜 로그인 회원은 자체 비밀번호가 없으므로 빈 문자열 저장
                    .nickname(nickname)
                    .role("ROLE_USER")
                    .authmethod(registrationId.toUpperCase())
                    .build();
            memberMapper.save(newMember);
            log.info("신규 소셜 회원 DB 자동 가입 완료: ID={}, Email={}", newMember.getId(), newMember.getEmail());
            return newMember;
        }

        return existingMember;
    }
}