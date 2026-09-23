package net.likelion.bebc25.itda.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.likelion.bebc25.itda.domain.Member;
import net.likelion.bebc25.itda.security.jwt.JwtProvider;
import net.likelion.bebc25.itda.security.principal.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;

    public OAuth2SuccessHandler(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // 1. CustomOAuth2UserService에서 반환한 통합 인증 객체 및 Member 엔티티 추출
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member member = userDetails.getMember();

        log.info("OAuth2 인증 성공 처리 시작: MemberId={}, Email={}", member.getId(), member.getEmail());

        // 2. 백엔드 서비스 전용 자체 JWT 액세스 토큰 생성
        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getEmail(), member.getRole());

        // 3. React 메인 페이지로 리다이렉트
        String targetUrl = UriComponentsBuilder
                .fromUriString("https://itda-web.netlify.app/")
//                .fromUriString("http://localhost:5173/")
                .queryParam("accessToken", accessToken)
                .build().toUriString();

        log.info("메인 SNS 페이지 리다이렉트 수행: {}", targetUrl);

        // 4. 브라우저 리다이렉트 실행
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}