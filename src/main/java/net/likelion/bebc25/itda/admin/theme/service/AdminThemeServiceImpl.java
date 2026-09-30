package net.likelion.bebc25.itda.admin.theme.service;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.admin.theme.dto.AdminThemeRequest;
import net.likelion.bebc25.itda.admin.theme.dto.AdminThemeResponse;
import net.likelion.bebc25.itda.admin.theme.mapper.AdminThemeMapper;
import net.likelion.bebc25.itda.s3.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminThemeServiceImpl implements AdminThemeService {

    private final AdminThemeMapper adminThemeMapper;
    private final S3Service s3Service;

    @Override
    public List<AdminThemeResponse> getAllThemes() {
        return adminThemeMapper.findAllThemes().stream()
                .map(theme -> AdminThemeResponse.from(
                        theme,
                        s3Service.getPresignedUrl(theme.thumbnailUrl())
                ))
                .toList();
    }

    @Override
    @Transactional
    public void createTheme(AdminThemeRequest request) {
        if (request.cssText() == null || request.cssText().isBlank()) {
            throw new IllegalArgumentException("CSS 텍스트는 필수입니다.");
        }

        String themeCode = request.themeCode();
        if (themeCode == null || themeCode.isBlank()) {
            themeCode = newThemeCode();
        } else if (adminThemeMapper.existsByThemeCode(themeCode)) {
            throw new IllegalArgumentException("이미 사용 중인 테마 코드입니다.");
        }

        adminThemeMapper.insertTheme(new AdminThemeRequest(
                request.themeName(),
                request.description(),
                request.price(),
                request.thumbnailUrl(),
                themeCode,
                request.cssText()
        ));
    }

    private String newThemeCode() {
        String themeCode;
        do {
            themeCode = "t" + Long.toString(System.nanoTime(), 36);
        } while (adminThemeMapper.existsByThemeCode(themeCode));
        return themeCode;
    }

    @Override
    @Transactional
    public void updateTheme(Long themeId, AdminThemeRequest request) {
        // 수정은 테마 코드를 바꾸지 않고 css_text만 갱신한다.
        if (request.cssText() == null || request.cssText().isBlank()) {
            throw new IllegalArgumentException("CSS 텍스트는 필수입니다.");
        }

        int updatedCount = adminThemeMapper.updateTheme(themeId, request);

        if (updatedCount == 0) {
            throw new IllegalArgumentException("존재하지 않는 테마입니다.");
        }
    }

    @Override
    @Transactional
    public void updateThemeStatus(Long themeId, String status) {
        if (!status.equals("ON_SALE") && !status.equals("HIDDEN")) {
            throw new IllegalArgumentException("올바르지 않은 테마 상태입니다.");
        }

        int updatedCount = adminThemeMapper.updateThemeStatus(themeId, status);

        if (updatedCount == 0) {
            throw new IllegalArgumentException("존재하지 않는 테마입니다.");
        }
    }
}
