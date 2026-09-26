package net.likelion.bebc25.itda.auth.service;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.auth.dto.RefreshTokenResponse;
import net.likelion.bebc25.itda.auth.mapper.RefreshTokenMapper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService{

    private final RefreshTokenMapper refreshTokenMapper;

    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Refresh Token 해시 생성에 실패했습니다.", e);
        }
    }

    public RefreshTokenResponse findValidToken(Long memberId, String hashToken) {
        return refreshTokenMapper.findValidToken(memberId, hashToken);
    }

    public void save(Long memberId, String tokenHash, LocalDateTime expiresAt) {
        RefreshTokenResponse refreshToken = new RefreshTokenResponse(null, memberId, tokenHash, expiresAt, null, null);

        refreshTokenMapper.save(refreshToken);
    }

    public void revoke(Long id) {
        refreshTokenMapper.revoke(id);
    }
}