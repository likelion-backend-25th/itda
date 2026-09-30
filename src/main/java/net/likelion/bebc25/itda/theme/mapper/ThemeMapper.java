package net.likelion.bebc25.itda.theme.mapper;

import net.likelion.bebc25.itda.theme.dto.ThemeDetailResponse;
import net.likelion.bebc25.itda.theme.dto.ThemeResponse;
import net.likelion.bebc25.itda.theme.dto.ThemeStylesResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ThemeMapper {

    Long findDefaultThemeId();

    // 테마 전체 목록 조회
    List<ThemeResponse> findAllThemes(@Param("memberId") Long memberId, @Param("limit") int limit, @Param("offset") int offset);

    // ID 기반 테마 상세 조회
    ThemeDetailResponse findById(@Param("memberId") Long memberId, @Param("themeId") Long themeId);

    // 전체 테마 개수
    long countThemes();

    // 회원의 보유 테마 등록
    void insertDefaultTheme(@Param("memberId") Long memberId, @Param("themeId") Long themeId);

    Integer findPriceById(@Param("themeId") Long themeId);

    String findStatusById(@Param("themeId") Long themeId);

    // 0원 테마 무료 수령
    void insertFreeThemePurchase(@Param("memberId") Long memberId, @Param("themeId") Long themeId);

    // 보유 테마 목록 조회
    List<ThemeResponse> findOwnedThemes(@Param("memberId") Long memberId, @Param("limit") int limit, @Param("offset") int offset);

    // 보유 테마 개수
    Long countOwnedThemes(@Param("memberId") Long memberId);

    // 스타일 API용 테마 조회
    ThemeStylesResponse.ThemeStyleRow findStyleById(@Param("themeId") Long themeId);

    // 구매(보유) 여부
    boolean existsPurchase(@Param("memberId") Long memberId, @Param("themeId") Long themeId);

    void updateMemberThemeId(@Param("memberId") Long memberId, @Param("themeId") Long themeId);
    void markThemeUsedIfNeeded(@Param("memberId") Long memberId, @Param("themeId") Long themeId);
}
