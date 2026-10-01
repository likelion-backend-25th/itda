package net.likelion.bebc25.itda.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.likelion.bebc25.itda.auth.service.RefreshTokenService;
import net.likelion.bebc25.itda.security.jwt.JwtProvider;
import net.likelion.bebc25.itda.security.principal.CustomUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final String frontendBaseUrl;

    public OAuth2SuccessHandler(
            JwtProvider jwtProvider,
            RefreshTokenService refreshTokenService,
            @Value("${app.frontend-base-url}") String frontendBaseUrl
    ) {
        this.jwtProvider = jwtProvider;
        this.refreshTokenService = refreshTokenService;
        this.frontendBaseUrl = frontendBaseUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // 1. CustomOAuth2UserService에서 반환한 통합 인증 객체 및 Member 엔티티 추출
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long memberId = userDetails.getMember().getId();
        String email = userDetails.getUsername();
        String role = userDetails.getMember().getRole();

        log.info("OAuth2 인증 성공 처리 시작: MemberId={}, Email={}", memberId, email);

        // 2. 백엔드 서비스 전용 자체 JWT 토큰 생성
        String accessToken = jwtProvider.createAccessToken(memberId, email, role);

        String refreshToken = jwtProvider.createRefreshToken(memberId);

        // 3. Refresh Token hash 값으로 DB 저장
        String tokenHash = refreshTokenService.hashToken(refreshToken);
        refreshTokenService.save(memberId, tokenHash, LocalDateTime.now().plusDays(7));

        // 4. Refresh Token을 HttpOnly Cookie로 설정
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/api/v1/auth")
                .maxAge(Duration.ofDays(7))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // 5. React 메인 페이지로 리다이렉트 (로컬/배포는 app.frontend-base-url)
        String targetUrl = UriComponentsBuilder
                .fromUriString(trimTrailingSlash(frontendBaseUrl) + "/")
                .queryParam("accessToken", accessToken)
                .build().toUriString();

        log.info("메인 SNS 페이지 리다이렉트 수행: {}", targetUrl);

        // 6. 브라우저 리다이렉트 실행
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private static String trimTrailingSlash(String url) {
        if (url == null || url.isBlank()) {
            return "";
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
