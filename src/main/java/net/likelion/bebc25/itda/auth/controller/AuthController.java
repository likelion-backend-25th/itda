package net.likelion.bebc25.itda.auth.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.auth.dto.RefreshTokenRequest;
import net.likelion.bebc25.itda.auth.dto.TokenResponse;
import net.likelion.bebc25.itda.domain.Member;
import net.likelion.bebc25.itda.member.dto.LoginRequest;
import net.likelion.bebc25.itda.security.jwt.JwtProvider;
import net.likelion.bebc25.itda.security.principal.CustomUserDetails;
import net.likelion.bebc25.itda.security.service.CustomUserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "로그인/인증 API", description = "인증 기능")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService userDetailsService;

    public AuthController(AuthenticationManager authenticationManager, JwtProvider jwtProvider, CustomUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
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

        // 5. 발급된 토큰 응답 반환 (Access Token 유효기간 1시간 = 3600초)
        TokenResponse response = TokenResponse.of(accessToken, refreshToken, 3600L);
        return ResponseEntity.ok(response);
    }

    // Refresh Token 기반 Access Token 갱신 엔드포인트
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();

        // 1. Refresh Token 서명 및 만료 유효성 검증
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new BadCredentialsException("유효하지 않거나 만료된 Refresh Token입니다.");
        }

        // 2. 토큰 페이로드에서 회원 PK 추출
        Long memberId = jwtProvider.getMemberId(refreshToken);

        // 3. UserDetailsService를 통해서 회원 조회
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserById(memberId);

        Member member = userDetails.getMember();

        // 4. 새 Access Token 및 Refresh Token 발급 (RTR 전략 적용)
        String newAccessToken = jwtProvider.createAccessToken(member.getId(), member.getEmail(), member.getRole());
        String newRefreshToken = jwtProvider.createRefreshToken(member.getId());

        TokenResponse response = TokenResponse.of(newAccessToken, newRefreshToken, 3600L);
        return ResponseEntity.ok(response);
    }
}