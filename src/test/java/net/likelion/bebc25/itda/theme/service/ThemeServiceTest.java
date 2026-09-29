package net.likelion.bebc25.itda.theme.service;

import net.likelion.bebc25.itda.dto.PageResponse;
import net.likelion.bebc25.itda.s3.S3Service;
import net.likelion.bebc25.itda.theme.dto.ThemeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
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
}
