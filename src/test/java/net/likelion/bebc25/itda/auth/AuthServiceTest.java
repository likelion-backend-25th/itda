package net.likelion.bebc25.itda.auth;

import net.likelion.bebc25.itda.auth.dto.AuthTokenResult;
import net.likelion.bebc25.itda.auth.dto.RefreshTokenResponse;
import net.likelion.bebc25.itda.auth.service.AuthServiceImpl;
import net.likelion.bebc25.itda.auth.service.RefreshTokenService;
import net.likelion.bebc25.itda.domain.Member;
import net.likelion.bebc25.itda.member.dto.LoginRequest;
import net.likelion.bebc25.itda.security.jwt.JwtProvider;
import net.likelion.bebc25.itda.security.principal.CustomUserDetails;
import net.likelion.bebc25.itda.security.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private CustomUserDetailsService userDetailsService;
    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthServiceImpl authService;

    private Member member;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .id(1L)
                .email("user@itda.com")
                .password("encoded")
                .nickname("잇다")
                .role("ROLE_USER")
                .status("ACTIVE")
                .build();
        userDetails = new CustomUserDetails(member);
    }

    @Test
    @DisplayName("login: 인증 성공 시 AccessToken 발급 및 RefreshToken 쿠키/DB 저장")
    void login_success() {
        // given
        LoginRequest request = new LoginRequest("user@itda.com", "password123");
        Authentication authentication = mock(Authentication.class);
        given(authentication.getPrincipal()).willReturn(userDetails);
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(authentication);
        given(jwtProvider.createAccessToken(1L, "user@itda.com", "ROLE_USER"))
                .willReturn("access-token");
        given(jwtProvider.createRefreshToken(1L)).willReturn("refresh-token");
        given(refreshTokenService.hashToken("refresh-token")).willReturn("hashed-refresh");

        // when
        AuthTokenResult result = authService.login(request);

        // then
        assertThat(result.tokenResponse().accessToken()).isEqualTo("access-token");
        assertThat(result.tokenResponse().tokenType()).isEqualTo("Bearer");
        assertThat(result.tokenResponse().expiresIn()).isEqualTo(3600L);

        ResponseCookie cookie = result.refreshCookie();
        assertThat(cookie.getName()).isEqualTo("refreshToken");
        assertThat(cookie.getValue()).isEqualTo("refresh-token");
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.isSecure()).isTrue();
        assertThat(cookie.getSameSite()).isEqualTo("None");
        assertThat(cookie.getPath()).isEqualTo("/api/v1/auth");
        assertThat(cookie.getMaxAge()).isEqualTo(Duration.ofDays(7));

        ArgumentCaptor<LocalDateTime> expiresAtCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(refreshTokenService).save(eq(1L), eq("hashed-refresh"), expiresAtCaptor.capture());
        assertThat(expiresAtCaptor.getValue()).isAfter(LocalDateTime.now().plusDays(6));
        assertThat(expiresAtCaptor.getValue()).isBefore(LocalDateTime.now().plusDays(8));
    }

    @Test
    @DisplayName("login: 비밀번호 오류 시 한글 BadCredentialsException")
    void login_badCredentials() {
        // given
        LoginRequest request = new LoginRequest("user@itda.com", "wrong");
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willThrow(new BadCredentialsException("Bad credentials"));

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("이메일 또는 비밀번호가 올바르지 않습니다.");

        verify(jwtProvider, never()).createAccessToken(any(), any(), any());
        verify(refreshTokenService, never()).save(any(), any(), any());
    }

    @Test
    @DisplayName("refresh: RTR - 기존 토큰 폐기 후 Access/Refresh 재발급")
    void refresh_success() {
        // given
        String oldRefresh = "old-refresh";
        RefreshTokenResponse saved = new RefreshTokenResponse(
                10L, 1L, "old-hash",
                LocalDateTime.now().plusDays(7),
                LocalDateTime.now(),
                null
        );

        given(jwtProvider.validateToken(oldRefresh)).willReturn(true);
        given(jwtProvider.getMemberId(oldRefresh)).willReturn(1L);
        given(refreshTokenService.hashToken(oldRefresh)).willReturn("old-hash");
        given(refreshTokenService.findValidToken(1L, "old-hash")).willReturn(saved);
        given(userDetailsService.loadUserById(1L)).willReturn(userDetails);
        given(jwtProvider.createAccessToken(1L, "user@itda.com", "ROLE_USER"))
                .willReturn("new-access");
        given(jwtProvider.createRefreshToken(1L)).willReturn("new-refresh");
        given(refreshTokenService.hashToken("new-refresh")).willReturn("new-hash");

        // when
        AuthTokenResult result = authService.refresh(oldRefresh);

        // then
        verify(refreshTokenService).revoke(10L);
        verify(refreshTokenService).save(eq(1L), eq("new-hash"), any(LocalDateTime.class));

        assertThat(result.tokenResponse().accessToken()).isEqualTo("new-access");
        assertThat(result.refreshCookie().getValue()).isEqualTo("new-refresh");
        assertThat(result.refreshCookie().getMaxAge()).isEqualTo(Duration.ofDays(7));
    }

    @Test
    @DisplayName("refresh: JWT 검증 실패 시 예외")
    void refresh_invalidJwt() {
        given(jwtProvider.validateToken("broken")).willReturn(false);

        assertThatThrownBy(() -> authService.refresh("broken"))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("유효하지 않거나 만료된 Refresh Token입니다.");

        verify(refreshTokenService, never()).revoke(any());
        verify(refreshTokenService, never()).save(any(), any(), any());
    }

    @Test
    @DisplayName("refresh: DB에 유효 토큰 없으면 예외")
    void refresh_tokenNotInDb() {
        given(jwtProvider.validateToken("refresh")).willReturn(true);
        given(jwtProvider.getMemberId("refresh")).willReturn(1L);
        given(refreshTokenService.hashToken("refresh")).willReturn("hash");
        given(refreshTokenService.findValidToken(1L, "hash")).willReturn(null);

        assertThatThrownBy(() -> authService.refresh("refresh"))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("유효하지 않은 Refresh Token입니다.");

        verify(refreshTokenService, never()).revoke(any());
    }

    @Test
    @DisplayName("logout: 유효 RefreshToken이면 DB revoke 후 삭제용 쿠키 반환")
    void logout_withValidToken() {
        RefreshTokenResponse saved = new RefreshTokenResponse(
                22L, 1L, "hash",
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now(),
                null
        );
        given(jwtProvider.validateToken("refresh")).willReturn(true);
        given(jwtProvider.getMemberId("refresh")).willReturn(1L);
        given(refreshTokenService.hashToken("refresh")).willReturn("hash");
        given(refreshTokenService.findValidToken(1L, "hash")).willReturn(saved);

        ResponseCookie cookie = authService.logout("refresh");

        verify(refreshTokenService).revoke(22L);
        assertThat(cookie.getName()).isEqualTo("refreshToken");
        assertThat(cookie.getValue()).isEmpty();
        assertThat(cookie.getMaxAge()).isEqualTo(Duration.ZERO);
        assertThat(cookie.getPath()).isEqualTo("/api/v1/auth");
    }

    @Test
    @DisplayName("logout: 토큰 null이면 revoke 없이 삭제용 쿠키만 반환")
    void logout_withNullToken() {
        ResponseCookie cookie = authService.logout(null);

        verify(refreshTokenService, never()).revoke(any());
        assertThat(cookie.getMaxAge()).isEqualTo(Duration.ZERO);
        assertThat(cookie.getValue()).isEmpty();
    }
}
