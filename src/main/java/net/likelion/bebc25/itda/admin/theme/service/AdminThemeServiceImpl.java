package net.likelion.bebc25.itda.admin.theme.service;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.admin.theme.dto.AdminThemeRequest;
import net.likelion.bebc25.itda.admin.theme.dto.AdminThemeResponse;
import net.likelion.bebc25.itda.admin.theme.mapper.AdminThemeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminThemeServiceImpl implements AdminThemeService {

    private final AdminThemeMapper adminThemeMapper;

    @Override
    public List<AdminThemeResponse> getAllThemes() {
        return adminThemeMapper.findAllThemes();
    }

    @Override
    @Transactional
    public void createTheme(AdminThemeRequest request) {
        if (adminThemeMapper.existsByThemeCode(request.themeCode())) {
            throw new IllegalArgumentException("이미 사용 중인 테마 코드입니다.");
        }

        adminThemeMapper.insertTheme(request);
    }

    @Override
    @Transactional
    public void updateTheme(Long themeId, AdminThemeRequest request) {
        if (!adminThemeMapper.existsByThemeCode(request.themeCode())
                || adminThemeMapper.findAllThemes().stream()
                .anyMatch(theme -> theme.themeCode().equals(request.themeCode())
                        && !theme.id().equals(themeId))) {
            throw new IllegalArgumentException("이미 사용 중인 테마 코드입니다.");
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