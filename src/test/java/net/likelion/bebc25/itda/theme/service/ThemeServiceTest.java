package net.likelion.bebc25.itda.theme.service;

import net.likelion.bebc25.itda.dto.PageResponse;
import net.likelion.bebc25.itda.s3.S3Service;
import net.likelion.bebc25.itda.theme.dto.ThemeResponse;
import net.likelion.bebc25.itda.theme.dto.ThemeStylesResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
public class ThemeServiceTest {

    @Autowired
    private ThemeService ThemeService;

    @MockitoBean
    private S3Service s3Service;

    @BeforeEach
    void setUp() {
        when(s3Service.getPresignedUrl(any())).thenAnswer(invocation -> {
            String key = invocation.getArgument(0);
            if (key == null || key.isBlank()) {
                return null;
            }
            return "https://example.com/" + key;
        });
    }

    @Test
    @DisplayName("게시글 상세 복합 조인 조회 테스트")
    void getAllThemesByIdTest() {
        // given: data.sql의 1번 게시글 (작성자: 1번 회원, 댓글: 3건 등록)
        Long memberId = 1L;
        int page = 1;
        int size = 6;

        // when
        PageResponse<ThemeResponse> themes = ThemeService.getAllThemes(memberId, page, size);

        // then
        assertThat(themes).isNotNull();
        assertThat(themes.getContent()).isNotEmpty();
        assertThat(themes.getContent().size()).isLessThanOrEqualTo(size);
        assertThat(themes.getPage()).isEqualTo(page);
        assertThat(themes.getSize()).isEqualTo(size);
        assertThat(themes.getTotalElements()).isPositive();
        assertThat(themes.getTotalPages()).isPositive();

        ThemeResponse latestTheme = themes.getContent().get(0);
        assertThat(latestTheme.id()).isNotNull();
        // status를 SELECT/DTO에 안 넣었으면 이 assert는 빼세요
    }

    @Test
    @DisplayName("보유 테마 스타일 조회 성공")
    void getThemeStyles_owned_success() {
        Long memberId = 1L;      // data.sql에 맞게 수정
        Long ownedThemeId = 2L;  // 구매 이력이 있는 theme id

        ThemeStylesResponse styles = ThemeService.getThemeStyles(memberId, ownedThemeId);

        assertThat(styles.themeId()).isEqualTo(ownedThemeId);
        assertThat(styles.themeCode()).isNotBlank();
        assertThat(styles.cssText()).contains(":root[data-theme=");
    }

    @Test
    @DisplayName("미보유 테마 스타일 조회 시 403")
    void getThemeStyles_notOwned_forbidden() {
        Long memberId = 1L;
        Long notOwnedThemeId = 9L; // 구매 없는 id로 교체

        assertThatThrownBy(() -> ThemeService.getThemeStyles(memberId, notOwnedThemeId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("보유하지 않은");
    }

    @Test
    @DisplayName("없는 테마 스타일 조회 시 404")
    void getThemeStyles_notFound() {
        assertThatThrownBy(() -> ThemeService.getThemeStyles(1L, 999999L))
                .isInstanceOf(NoSuchElementException.class);
    }
}
