package net.likelion.bebc25.itda.auth.controller;

import com.nimbusds.oauth2.sdk.TokenResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.domain.Member;
import net.likelion.bebc25.itda.member.dto.LoginRequest;
import net.likelion.bebc25.itda.member.dto.LoginResponse;
import net.likelion.bebc25.itda.security.jwt.JwtProvider;
import net.likelion.bebc25.itda.security.principal.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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

    public AuthController(AuthenticationManager authenticationManager, JwtProvider jwtProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }

//    @PostMapping("/login")
//    public ResponseEntity<LoginResponse> login(
//            @RequestBody LoginRequest request
//    ) {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        request.getEmail(),
//                        request.getPassword()
//                )
//        );
//
//        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//
//        Long memberId = userDetails.getMember().getId();
//        String email = userDetails.getUsername();
//        String role = userDetails.getMember().getRole();
//
//        // 4. JWT 토큰 생성
//        String accessToken = jwtProvider.createAccessToken(memberId, email, role);
//        String refreshToken = jwtProvider.createRefreshToken(memberId);
//
//        // 5. 발급된 토큰 응답 반환 (Access Token 유효기간 1시간 = 3600초)
//        TokenResponse response = TokenResponse.of(accessToken, refreshToken, 3600L);
//        return ResponseEntity.ok(new LoginResponse(accessToken));
//    }
}