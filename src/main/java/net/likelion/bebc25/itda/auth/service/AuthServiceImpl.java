package net.likelion.bebc25.itda.auth.service;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.auth.dto.AuthTokenResult;
import net.likelion.bebc25.itda.auth.dto.RefreshTokenResponse;
import net.likelion.bebc25.itda.auth.dto.TokenResponse;
import net.likelion.bebc25.itda.domain.Member;
import net.likelion.bebc25.itda.member.dto.LoginRequest;
import net.likelion.bebc25.itda.security.jwt.JwtProvider;
import net.likelion.bebc25.itda.security.principal.CustomUserDetails;
import net.likelion.bebc25.itda.security.service.CustomUserDetailsService;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final long ACCESS_EXPIRES_IN_SECONDS = 3600L; // 1시간
    private static final Duration REFRESH_COOKIE_MAX_AGE = Duration.ofDays(7);

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public AuthTokenResult login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member member = userDetails.getMember();

        // 새 AccessToken 및 Refresh Token 발급
        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getEmail(), member.getRole());
        String refreshToken = jwtProvider.createRefreshToken(member.getId());

        // RefreshToken을 hash값으로 DB에 저장
        String tokenHash = refreshTokenService.hashToken(refreshToken);
        refreshTokenService.save(member.getId(), tokenHash, LocalDateTime.now().plusDays(7));

        return new AuthTokenResult(
                TokenResponse.of(accessToken, ACCESS_EXPIRES_IN_SECONDS),
                refreshCookie(refreshToken, REFRESH_COOKIE_MAX_AGE)
        );
    }

    @Override
    @Transactional
    public AuthTokenResult refresh(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new BadCredentialsException("유효하지 않거나 만료된 Refresh Token입니다.");
        }

        Long memberId = jwtProvider.getMemberId(refreshToken);

        // RTR 상태 검증
        String tokenHash = refreshTokenService.hashToken(refreshToken);
        RefreshTokenResponse savedToken = refreshTokenService.findValidToken(memberId, tokenHash);
        if (savedToken == null) {
            throw new BadCredentialsException("유효하지 않은 Refresh Token입니다.");
        }

        // 기존 RefreshToken 폐지
        refreshTokenService.revoke(savedToken.getId());

        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserById(memberId);
        Member member = userDetails.getMember();

        // AccessToken 및 Refresh Token 재발급
        String newAccessToken = jwtProvider.createAccessToken(member.getId(), member.getEmail(), member.getRole());
        String newRefreshToken = jwtProvider.createRefreshToken(member.getId());

        // 재발급한 RefreshToken을 hash값으로 DB에 저장
        String newTokenHash = refreshTokenService.hashToken(newRefreshToken);
        refreshTokenService.save(memberId, newTokenHash, LocalDateTime.now().plusDays(7));


        return new AuthTokenResult(
                TokenResponse.of(newAccessToken, ACCESS_EXPIRES_IN_SECONDS),
                refreshCookie(newRefreshToken, REFRESH_COOKIE_MAX_AGE)
        );
    }

    @Override
    @Transactional
    public ResponseCookie logout(String refreshTokenOrNull) {
        if (refreshTokenOrNull != null && jwtProvider.validateToken(refreshTokenOrNull)) {
            Long memberId = jwtProvider.getMemberId(refreshTokenOrNull);

            String tokenHash = refreshTokenService.hashToken(refreshTokenOrNull);
            RefreshTokenResponse saved = refreshTokenService.findValidToken(memberId, tokenHash);
            if (saved != null) {
                refreshTokenService.revoke(saved.getId());
            }
        }
        return refreshCookie("", Duration.ZERO);
    }

    private ResponseCookie refreshCookie(String token, Duration maxAge) {
        return ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/api/v1/auth")
                .maxAge(maxAge)
                .build();
    }
}
