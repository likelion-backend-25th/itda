package net.likelion.bebc25.itda.auth.dto;

import org.springframework.http.ResponseCookie;

public record AuthTokenResult(
        TokenResponse tokenResponse,
        ResponseCookie refreshCookie
) {}
