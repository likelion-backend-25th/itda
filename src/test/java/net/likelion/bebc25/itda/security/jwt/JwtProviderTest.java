package net.likelion.bebc25.itda.security.jwt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JwtProviderTest {

    @Autowired
    private JwtProvider jwtProvider;

    @Value("${jwt.secret}")
    private String secret;

    @Test
    @DisplayName("Access Token 생성 및 클레임 추출 검증")
    void createAccessTokenAndParseClaimsTest() {
        // given
        Long memberId = 2L;
        String email = "user1@example.com";
        String role = "ROLE_USER";

        // when
        String token = jwtProvider.createAccessToken(memberId, email, role);

        System.out.println(token);
        System.out.println("=============");
        // then
        assertThat(token).isNotBlank();
        assertThat(jwtProvider.validateToken(token)).isTrue();
        assertThat(jwtProvider.getMemberId(token)).isEqualTo(memberId);
        assertThat(jwtProvider.getEmail(token)).isEqualTo(email);
    }

    @Test
    @DisplayName("Refresh Token 생성 및 유효성 검증")
    void createRefreshTokenTest() {
        // given
        Long memberId = 1L;

        // when
        String refreshToken = jwtProvider.createRefreshToken(memberId);

        System.out.println(refreshToken);
        // then
        assertThat(refreshToken).isNotBlank();
        assertThat(jwtProvider.validateToken(refreshToken)).isTrue();
        assertThat(jwtProvider.getMemberId(refreshToken)).isEqualTo(memberId);
    }

    @Test
    @DisplayName("변조된 토큰 검증 시 실패 반환")
    void validateTamperedTokenTest() {
        // given
        String token = jwtProvider.createAccessToken(2L, "user1@example.com", "ROLE_USER");
        String tamperedToken = token + "invalid";

        // when
        boolean isValid = jwtProvider.validateToken(tamperedToken);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("만료된 토큰 검증 시 실패 반환")
    void validateExpiredTokenTest() {
        // given: application.yml의 secret 값을 재사용하되, 만료 시간을 음수로 설정한 테스트용 Provider 생성
        JwtProvider expiredProvider = new JwtProvider(secret, -1000L, -1000L);
        String expiredToken = expiredProvider.createAccessToken(2L, "user1@example.com", "ROLE_USER");

        // when
        boolean isValid = jwtProvider.validateToken(expiredToken);

        // then
        assertThat(isValid).isFalse();
    }
}
