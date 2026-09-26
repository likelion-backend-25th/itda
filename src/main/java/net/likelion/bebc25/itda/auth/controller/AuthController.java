package net.likelion.bebc25.itda.auth.controller;

import com.nimbusds.oauth2.sdk.token.RefreshToken;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import net.likelion.bebc25.itda.auth.dto.RefreshTokenResponse;
import net.likelion.bebc25.itda.auth.dto.TokenResponse;
import net.likelion.bebc25.itda.auth.service.RefreshTokenServiceImpl;
import net.likelion.bebc25.itda.domain.Member;
import net.likelion.bebc25.itda.member.dto.LoginRequest;
import net.likelion.bebc25.itda.security.jwt.JwtProvider;
import net.likelion.bebc25.itda.security.principal.CustomUserDetails;
import net.likelion.bebc25.itda.security.service.CustomUserDetailsService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Tag(name = "로그인/인증 API", description = "인증 기능")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService userDetailsService;
    private final RefreshTokenServiceImpl refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager, JwtProvider jwtProvider, CustomUserDetailsService userDetailsService, RefreshTokenServiceImpl refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        Authentication unauthenticatedToken = new UsernamePasswordAuthenticationToken(request.email(), request.password());

        // 2. AuthenticationManager를 통한 인증 검증 위임
        Authentication authentication = authenticationManager.authenticate(unauthenticatedToken);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long memberId = userDetails.getMember().getId();
        String email = userDetails.getUsername();
        String role = userDetails.getMember().getRole();

        // 4. JWT 토큰 생성
        String accessToken = jwtProvider.createAccessToken(memberId, email, role);
        String refreshToken = jwtProvider.createRefreshToken(memberId);

        // 5. Refresh Token hash 값으로 DB 저장
        String tokenHash = refreshTokenService.hashToken(refreshToken);
        refreshTokenService.save(memberId, tokenHash, LocalDateTime.now().plusDays(7));

        // 6. Refresh Token을 HttpOnly Coolie로 설정
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/api/v1/auth")
                .maxAge(Duration.ofDays(7))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE,cookie.toString());

        // 7. 발급된 토큰 응답 반환 (Access Token 유효기간 1시간 = 3600초)
        TokenResponse tokenResponse = TokenResponse.of(accessToken, 3600L);
        return ResponseEntity.ok(tokenResponse);
    }

    // Refresh Token 기반 Access Token 갱신 엔드포인트
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
        // 1. Refresh Token 서명 및 만료 유효성 검증
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new BadCredentialsException("유효하지 않거나 만료된 Refresh Token입니다.");
        }

        // 2. 토큰 페이로드에서 회원 PK 추출
        Long memberId = jwtProvider.getMemberId(refreshToken);

        // 3. RTR 상태 검증
        String tokenHash = refreshTokenService.hashToken(refreshToken);
        RefreshTokenResponse savedToken = refreshTokenService.findValidToken(memberId, tokenHash);

        if (savedToken == null) {
            throw new BadCredentialsException("유효하지 않은 Refresh Token입니다.");
        }

        // 4. 기존 Refresh Token 폐지
        refreshTokenService.revoke(savedToken.getId());

        // 5. UserDetailsService를 통해서 회원 조회
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserById(memberId);

        Member member = userDetails.getMember();

        // 6. 새 Access Token 및 Refresh Token 발급 (RTR 전략 적용)
        String newAccessToken = jwtProvider.createAccessToken(member.getId(), member.getEmail(), member.getRole());
        String newRefreshToken = jwtProvider.createRefreshToken(member.getId());

        // 7. 새 Refresh Token hash 값으로 DB 저장
        String newTokenHash = refreshTokenService.hashToken(newRefreshToken);
        refreshTokenService.save(memberId, newTokenHash, LocalDateTime.now().plusDays(7));

        // 8. 새로운 Refresh Token을 HttpOnly Cookie로 설정
        ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true) // JS 접근 차단 + HTTPS 전송 + cross-site Cookie 허용
                .secure(true)
                .sameSite("None")
                .path("/api/v1/auth")
                .maxAge(Duration.ofDays(7))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // 9. Access Token만 Response Body로 반환
        TokenResponse tokenResponse = TokenResponse.of(newAccessToken, 3600L);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
        // 1. 쿠키에 refresh가 있으면 DB에서 revoke (RTR 정리)
        if (refreshToken != null && jwtProvider.validateToken(refreshToken)) {
            Long memberId = jwtProvider.getMemberId(refreshToken);
            String tokenHash = refreshTokenService.hashToken(refreshToken);

            RefreshTokenResponse saved = refreshTokenService.findValidToken(memberId, tokenHash);

            if (saved != null) {
                refreshTokenService.revoke(saved.getId());
            }
        }

        // 2. 브라우저 refresh 쿠키 삭제 (로그인 때와 속성 동일하게)
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/api/v1/auth")  // 로그인과 반드시 동일
                .maxAge(0)             // 즉시 만료
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.noContent().build(); // 204
    }
}