package net.likelion.bebc25.itda.theme.service;

import net.likelion.bebc25.itda.dto.PageResponse;
import net.likelion.bebc25.itda.theme.dto.ThemeResponse;

import java.util.List;

public interface ThemeService {

    // 전체 테마 목록  조회
    PageResponse<ThemeResponse> getAllThemesById(Long memberId, int page, int size);
}
