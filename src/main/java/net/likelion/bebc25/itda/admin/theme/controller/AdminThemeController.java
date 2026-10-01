package net.likelion.bebc25.itda.admin.theme.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.admin.theme.dto.AdminThemeRequest;
import net.likelion.bebc25.itda.admin.theme.dto.AdminThemeResponse;
import net.likelion.bebc25.itda.admin.theme.service.AdminThemeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "관리자 테마 API", description = "관리자 테마 등록·수정·상태 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/themes")
public class AdminThemeController {

    private final AdminThemeService adminThemeService;

    // 테마 목록 조회
    @Operation(
            summary = "테마 목록 조회",
            description = "관리자용 전체 테마 목록을 조회한다."
    )
    @GetMapping
    public ResponseEntity<List<AdminThemeResponse>> getAllThemes() {
        return ResponseEntity.ok(adminThemeService.getAllThemes());
    }

    // 테마 등록
    @Operation(
            summary = "테마 등록",
            description = "새 테마를 등록한다."
    )
    @PostMapping
    public ResponseEntity<Void> createTheme(
            @Valid @RequestPart("request") AdminThemeRequest request,
            @RequestPart(value = "themeImage", required = false)
            MultipartFile themeImage
    ) {
        adminThemeService.createTheme(request, themeImage);
        return ResponseEntity.status(201).build();
    }

    // 테마 수정 — 게시글 수정과 동일하게 multipart (request JSON + themeImage?)
    @Operation(
            summary = "테마 수정",
            description = "기존 테마 정보를 수정한다. 새 이미지가 없으면 기존 썸네일을 유지한다."
    )
    @PutMapping("/{themeId}")
    public ResponseEntity<Void> updateTheme(
            @PathVariable Long themeId,
            @Valid @RequestPart("request") AdminThemeRequest request,
            @RequestPart(value = "themeImage", required = false) MultipartFile themeImage
    ) {
        adminThemeService.updateTheme(themeId, request, themeImage);
        return ResponseEntity.noContent().build();
    }

    // 테마 활성화/비활성화
    @Operation(
            summary = "테마 상태 변경",
            description = "테마 활성화/비활성화 상태를 변경한다."
    )
    @PatchMapping("/{themeId}/status")
    public ResponseEntity<Void> updateThemeStatus(
            @PathVariable Long themeId,
            @RequestParam String status) {
        adminThemeService.updateThemeStatus(themeId, status);
        return ResponseEntity.noContent().build();
    }
}
