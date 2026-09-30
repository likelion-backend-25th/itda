package net.likelion.bebc25.itda.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.auth.dto.AuthTokenResult;
import net.likelion.bebc25.itda.auth.dto.TokenResponse;
import net.likelion.bebc25.itda.auth.service.AuthService;
import net.likelion.bebc25.itda.member.dto.LoginRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "로그인/인증 API", description = "인증 기능")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호로 로그인하고 Access Token과 Refresh Token을 발급합니다."
    )
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        AuthTokenResult result = authService.login(request);

        response.addHeader(HttpHeaders.SET_COOKIE, result.refreshCookie().toString());
        return ResponseEntity.ok(result.tokenResponse());
    }

    @Operation(
            summary = "Access Token 재발급",
            description = "HttpOnly Cookie의 Refresh Token을 검증하여 새로운 Access Token과 Refresh Token을 발급합니다."
    )
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
        AuthTokenResult result = authService.refresh(refreshToken);

        response.addHeader(HttpHeaders.SET_COOKIE, result.refreshCookie().toString());
        return ResponseEntity.ok(result.tokenResponse());
    }

    @Operation(
            summary = "로그아웃",
            description = "Refresh Token을 폐기하고 클라이언트의 Refresh Token Cookie를 삭제합니다."
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
        ResponseCookie clearCookie = authService.logout(refreshToken);

        response.addHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());
        return ResponseEntity.noContent().build();
    }
}
