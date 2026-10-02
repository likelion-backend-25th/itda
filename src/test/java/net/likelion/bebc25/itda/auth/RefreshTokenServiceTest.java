package net.likelion.bebc25.itda.auth;

import net.likelion.bebc25.itda.auth.dto.RefreshTokenResponse;
import net.likelion.bebc25.itda.auth.mapper.RefreshTokenMapper;
import net.likelion.bebc25.itda.auth.service.RefreshTokenServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenMapper refreshTokenMapper;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    @Test
    @DisplayName("hashToken: SHA-256 hex 문자열을 반환한다")
    void hashToken_returnsSha256Hex() throws Exception {
        String token = "refresh-token-sample";

        String actual = refreshTokenService.hashToken(token);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String expected = HexFormat.of().formatHex(
                digest.digest(token.getBytes(StandardCharsets.UTF_8))
        );

        assertThat(actual).isEqualTo(expected);
        assertThat(actual).hasSize(64);
        assertThat(actual).matches("[0-9a-f]+");
    }

    @Test
    @DisplayName("hashToken: 같은 입력이면 항상 같은 해시를 반환한다")
    void hashToken_isDeterministic() {
        String token = "same-token";

        String first = refreshTokenService.hashToken(token);
        String second = refreshTokenService.hashToken(token);

        assertThat(first).isEqualTo(second);
    }

    @Test
    @DisplayName("hashToken: 다른 입력이면 다른 해시를 반환한다")
    void hashToken_differentInput_differentHash() {
        String hash1 = refreshTokenService.hashToken("token-a");
        String hash2 = refreshTokenService.hashToken("token-b");

        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    @DisplayName("findValidToken: Mapper 조회 결과를 그대로 반환한다")
    void findValidToken_delegatesToMapper() {
        RefreshTokenResponse saved = new RefreshTokenResponse(
                1L, 10L, "abc-hash",
                LocalDateTime.now().plusDays(7),
                LocalDateTime.now(),
                null
        );
        given(refreshTokenMapper.findValidToken(10L, "abc-hash")).willReturn(saved);

        RefreshTokenResponse result = refreshTokenService.findValidToken(10L, "abc-hash");

        assertThat(result).isSameAs(saved);
        verify(refreshTokenMapper).findValidToken(10L, "abc-hash");
    }

    @Test
    @DisplayName("findValidToken: 유효 토큰이 없으면 null을 반환한다")
    void findValidToken_notFound_returnsNull() {
        given(refreshTokenMapper.findValidToken(10L, "missing")).willReturn(null);

        RefreshTokenResponse result = refreshTokenService.findValidToken(10L, "missing");

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("save: memberId·tokenHash·expiresAt으로 RefreshToken을 저장한다")
    void save_buildsEntityAndCallsMapper() {
        LocalDateTime expiresAt = LocalDateTime.of(2026, 10, 9, 12, 0);

        refreshTokenService.save(10L, "hashed-token", expiresAt);

        ArgumentCaptor<RefreshTokenResponse> captor =
                ArgumentCaptor.forClass(RefreshTokenResponse.class);
        verify(refreshTokenMapper).save(captor.capture());

        RefreshTokenResponse saved = captor.getValue();
        assertThat(saved.getId()).isNull();
        assertThat(saved.getMemberId()).isEqualTo(10L);
        assertThat(saved.getTokenHash()).isEqualTo("hashed-token");
        assertThat(saved.getExpiresAt()).isEqualTo(expiresAt);
        assertThat(saved.getCreatedAt()).isNull();
        assertThat(saved.getRevokedAt()).isNull();
    }

    @Test
    @DisplayName("revoke: Mapper에 id를 전달해 폐기한다")
    void revoke_delegatesToMapper() {
        refreshTokenService.revoke(55L);

        verify(refreshTokenMapper).revoke(55L);
    }
}
