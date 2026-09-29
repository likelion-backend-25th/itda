package net.likelion.bebc25.itda.theme.service;

import net.likelion.bebc25.itda.dto.PageResponse;
import net.likelion.bebc25.itda.theme.dto.ThemeDetailResponse;
import net.likelion.bebc25.itda.theme.dto.ThemeResponse;
import net.likelion.bebc25.itda.theme.dto.ThemeStylesResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ThemeService {

    // 기본 테마의 id 조회
    Long getDefaultThemeId();

    // 전체 테마 목록 조회
    PageResponse<ThemeResponse> getAllThemes(Long memberId, int page, int size);

    // ID 기반 테마 상세 조회
    ThemeDetailResponse getThemeById(Long memberId, Long themeId);

    // 회원의 보유 테마 등록
    void insertDefaultTheme(Long memberId, Long themeId);

    // 보유 테마 목록 조회
    PageResponse<ThemeResponse> getOwnedThemes(Long memberId, int page, int size);

    ThemeStylesResponse getThemeStyles(Long memberId, Long themeId);

    ThemeDetailResponse applyTheme(Long memberId, Long themeId);

    /** 가격 0원인 ON_SALE 테마를 PortOne 없이 보유 등록 */
    ThemeDetailResponse claimFreeTheme(Long memberId, Long themeId);
}
