package net.likelion.bebc25.itda.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenResponse {

    private Long id;
    private Long memberId;
    private String tokenHash;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime revokedAt;

}