package net.likelion.bebc25.itda.theme.mapper;

import net.likelion.bebc25.itda.member.mapper.MemberMapper;
import net.likelion.bebc25.itda.s3.S3Service;
import net.likelion.bebc25.itda.theme.dto.ThemeDetailResponse;
import net.likelion.bebc25.itda.theme.dto.ThemeResponse;
import net.likelion.bebc25.itda.theme.dto.ThemeStylesResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ThemeMapperTest {

    @Autowired
    private ThemeMapper themeMapper;

    @Autowired
    private MemberMapper memberMapper;

    @MockitoBean
    private S3Service s3Service;

    @Test
    @DisplayName("findDefaultThemeId: is_default=true 테마 id를 반환한다")
    void findDefaultThemeId() {
        Long defaultThemeId = themeMapper.findDefaultThemeId();

        assertThat(defaultThemeId).isEqualTo(1L);
    }

    @Test
    @DisplayName("findAllThemes: ON_SALE 테마만 조회하고 보유/적용 여부를 포함한다")
    void findAllThemes_withMember() {
        List<ThemeResponse> themes = themeMapper.findAllThemes(1L, 20, 0, null);

        assertThat(themes).isNotEmpty();
        assertThat(themes).noneMatch(t -> t.themeCode().equals("HIDDEN"));

        ThemeResponse ocean = themes.stream()
                .filter(t -> t.id().equals(3L))
                .findFirst()
                .orElseThrow();
        assertThat(ocean.isOwned()).isTrue();
        assertThat(ocean.isApplied()).isTrue(); // user1 theme_id=3
    }

    @Test
    @DisplayName("findAllThemes: keyword로 테마명을 필터한다")
    void findAllThemes_withKeyword() {
        List<ThemeResponse> themes = themeMapper.findAllThemes(1L, 20, 0, "오션");

        assertThat(themes).isNotEmpty();
        assertThat(themes).allMatch(t -> t.themeName().contains("오션"));
    }

    @Test
    @DisplayName("countThemes: ON_SALE 테마 개수를 반환한다")
    void countThemes() {
        long count = themeMapper.countThemes(null);
        long keywordCount = themeMapper.countThemes("오션");

        assertThat(count).isPositive();
        assertThat(keywordCount).isPositive();
        assertThat(keywordCount).isLessThanOrEqualTo(count);
        // HIDDEN(9) 제외 → 시드 ON_SALE 8개
        assertThat(count).isEqualTo(8L);
    }

    @Test
    @DisplayName("findById: 테마 상세와 보유/적용 여부를 반환한다")
    void findById_detail() {
        ThemeDetailResponse detail = themeMapper.findById(1L, 3L);

        assertThat(detail).isNotNull();
        assertThat(detail.id()).isEqualTo(3L);
        assertThat(detail.themeName()).contains("오션");
        assertThat(detail.isOwned()).isTrue();
        assertThat(detail.isApplied()).isTrue();
        assertThat(detail.createdAt()).isNotNull();
    }

    @Test
    @DisplayName("findPriceById / findStatusById")
    void findPriceAndStatus() {
        assertThat(themeMapper.findPriceById(3L)).isEqualTo(3000);
        assertThat(themeMapper.findStatusById(3L)).isEqualTo("ON_SALE");
        assertThat(themeMapper.findPriceById(1L)).isEqualTo(0);
        assertThat(themeMapper.findStatusById(9L)).isEqualTo("HIDDEN");
    }

    @Test
    @DisplayName("insertDefaultTheme: 보유 테마를 등록한다")
    void insertDefaultTheme() {
        // member 3은 theme 2(다크) 미보유
        assertThat(themeMapper.existsPurchase(3L, 2L)).isFalse();

        themeMapper.insertDefaultTheme(3L, 2L);

        assertThat(themeMapper.existsPurchase(3L, 2L)).isTrue();
    }

    @Test
    @DisplayName("insertFreeThemePurchase: 미보유 0원 테마를 수령한다")
    void insertFreeThemePurchase() {
        assertThat(themeMapper.existsPurchase(3L, 2L)).isFalse();

        themeMapper.insertFreeThemePurchase(3L, 2L);

        assertThat(themeMapper.existsPurchase(3L, 2L)).isTrue();
    }

    @Test
    @DisplayName("insertFreeThemePurchase: 이미 보유 중이면 중복 insert하지 않는다")
    void insertFreeThemePurchase_idempotent() {
        long before = themeMapper.countOwnedThemes(1L);

        themeMapper.insertFreeThemePurchase(1L, 3L); // 이미 보유

        assertThat(themeMapper.countOwnedThemes(1L)).isEqualTo(before);
    }

    @Test
    @DisplayName("findOwnedThemes / countOwnedThemes: 보유 테마 목록·개수")
    void findOwnedThemes() {
        Long count = themeMapper.countOwnedThemes(1L);
        List<ThemeResponse> owned = themeMapper.findOwnedThemes(1L, 20, 0);

        assertThat(count).isPositive();
        assertThat(owned).hasSize(count.intValue());
        assertThat(owned).allMatch(ThemeResponse::isOwned);

        ThemeResponse applied = owned.stream()
                .filter(t -> Boolean.TRUE.equals(t.isApplied()))
                .findFirst()
                .orElseThrow();
        assertThat(applied.id()).isEqualTo(3L);
    }

    @Test
    @DisplayName("findStyleById: 테마 코드·CSS를 조회한다")
    void findStyleById() {
        ThemeStylesResponse.ThemeStyleRow row = themeMapper.findStyleById(3L);

        assertThat(row).isNotNull();
        assertThat(row.id()).isEqualTo(3L);
        assertThat(row.themeCode()).isEqualTo("OCEAN");
        assertThat(row.cssText()).contains(":root[data-theme=");
        assertThat(row.isDefault()).isFalse();
    }

    @Test
    @DisplayName("existsPurchase: 보유 여부를 반환한다")
    void existsPurchase() {
        assertThat(themeMapper.existsPurchase(1L, 3L)).isTrue();
        assertThat(themeMapper.existsPurchase(1L, 9L)).isFalse();
    }

    @Test
    @DisplayName("updateMemberThemeId: 회원 적용 테마를 변경한다")
    void updateMemberThemeId() {
        themeMapper.updateMemberThemeId(1L, 1L);

        assertThat(memberMapper.findById(1L).getThemeId()).isEqualTo(1L);

        ThemeDetailResponse detail = themeMapper.findById(1L, 1L);
        assertThat(detail.isApplied()).isTrue();
    }

    @Test
    @DisplayName("markThemeUsedIfNeeded: is_used=false 보유 건을 true로 바꾼다")
    void markThemeUsedIfNeeded() {
        // user1의 theme 5(선셋)는 is_used=FALSE
        themeMapper.markThemeUsedIfNeeded(1L, 5L);

        // 적용 후 다시 호출해도 에러 없이 동작 (이미 TRUE면 갱신 0건)
        themeMapper.markThemeUsedIfNeeded(1L, 5L);

        assertThat(themeMapper.existsPurchase(1L, 5L)).isTrue();
    }
}
