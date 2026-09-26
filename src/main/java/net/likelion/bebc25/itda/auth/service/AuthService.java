package net.likelion.bebc25.itda.auth.service;

import net.likelion.bebc25.itda.auth.dto.AuthTokenResult;
import net.likelion.bebc25.itda.member.dto.LoginRequest;
import org.springframework.http.ResponseCookie;

public interface AuthService {

    // 이메일 로그인 - access body + refresh 쿠키
    AuthTokenResult login(LoginRequest request);

    // RTR: 검증, revoke, 재발급, save
    AuthTokenResult refresh(String refreshToken);

    // DB revoke + maxAge=0으로 삭제용 쿠키 반환
    ResponseCookie logout(String refreshTokenOrNull);
}
