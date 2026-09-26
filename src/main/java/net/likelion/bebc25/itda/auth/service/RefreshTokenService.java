package net.likelion.bebc25.itda.auth.service;

import net.likelion.bebc25.itda.auth.dto.RefreshTokenResponse;

import java.time.LocalDateTime;

public interface RefreshTokenService {

    String hashToken(String token);

    void save(Long memberId, String tokenHash, LocalDateTime expiresAt);

    RefreshTokenResponse findValidToken(Long memberId, String hashToken);

    void revoke(Long id);
}
