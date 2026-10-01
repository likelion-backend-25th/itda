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
}