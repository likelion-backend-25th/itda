package net.likelion.bebc25.itda.admin.theme.service;

import net.likelion.bebc25.itda.admin.theme.dto.AdminThemeRequest;
import net.likelion.bebc25.itda.admin.theme.dto.AdminThemeResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminThemeService {

    List<AdminThemeResponse> getAllThemes();

    void createTheme(AdminThemeRequest request, MultipartFile themeImage);

    void updateTheme(Long themeId, AdminThemeRequest request, MultipartFile themeImage);

    void updateThemeStatus(Long themeId, String status);

    /** 해당 테마를 유일한 기본 테마로 지정한다. */
    void setDefaultTheme(Long themeId);
}