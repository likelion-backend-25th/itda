package net.likelion.bebc25.itda.admin.theme.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.admin.theme.dto.AdminThemeRequest;
import net.likelion.bebc25.itda.admin.theme.dto.AdminThemeResponse;
import net.likelion.bebc25.itda.admin.theme.service.AdminThemeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/themes")
public class AdminThemeController {

    private final AdminThemeService adminThemeService;

    // 테마 목록 조회
    @GetMapping
    public ResponseEntity<List<AdminThemeResponse>> getAllThemes() {
        return ResponseEntity.ok(adminThemeService.getAllThemes());
    }

    // 테마 등록
    @PostMapping
    public ResponseEntity<Void> createTheme(
            @Valid @RequestBody AdminThemeRequest request) {
        adminThemeService.createTheme(request);
        return ResponseEntity.status(201).build();
    }

    // 테마 수정
    @PutMapping("/{themeId}")
    public ResponseEntity<Void> updateTheme(
            @PathVariable Long themeId,
            @Valid @RequestBody AdminThemeRequest request) {
        adminThemeService.updateTheme(themeId, request);
        return ResponseEntity.noContent().build();
    }

    // 테마 활성화/비활성화
    @PatchMapping("/{themeId}/status")
    public ResponseEntity<Void> updateThemeStatus(
            @PathVariable Long themeId,
            @RequestParam String status) {
        adminThemeService.updateThemeStatus(themeId, status);
        return ResponseEntity.noContent().build();
    }
}