package net.likelion.bebc25.itda.theme.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.dto.PageResponse;
import net.likelion.bebc25.itda.post.dto.PostCreateRequest;
import net.likelion.bebc25.itda.post.dto.PostResponse;
import net.likelion.bebc25.itda.post.service.PostService;
import net.likelion.bebc25.itda.security.principal.CustomUserDetails;
import net.likelion.bebc25.itda.theme.dto.ThemeDetailResponse;
import net.likelion.bebc25.itda.theme.dto.ThemeResponse;
import net.likelion.bebc25.itda.theme.service.ThemeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Tag(name = "테마 API", description = "인증 기능")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/themes")
public class ThemeController {

    private final ThemeService themeService;

    @Operation(
            summary = "테마 목록 조회",
            description = "판매중 테마를 최신순으로 페이징 조회합니다. 한 페이지 기본 6개."
    )
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = PageResponse.class))
    )
    @GetMapping
    public ResponseEntity<PageResponse<ThemeResponse>> getThemeList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "페이지 번호 (1부터)", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "페이지 크기", example = "6")
            @RequestParam(defaultValue = "6") int size
    ) {
        Long memberId = userDetails != null ? userDetails.getId() : null;

        PageResponse<ThemeResponse> themes = themeService.getAllThemes(memberId, page, size);
        return ResponseEntity.ok(themes);
    }

    @Operation(
            summary = "내 보유 테마 조회",
            description = "보유하고 있는 테마 목록을 조회합니다."
    )
    @GetMapping("/owned")
    public ResponseEntity<PageResponse<ThemeResponse>> getOwnedThemeList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "페이지 번호 (1부터)", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "페이지 크기", example = "6")
            @RequestParam(defaultValue = "6") int size
    ) {
        Long memberId = userDetails.getId();

        PageResponse<ThemeResponse> themes = themeService.getOwnedThemes(memberId, page, size);
        return ResponseEntity.ok(themes);
    }

    @Operation(
            summary = "테마 상세 조회",
            description = "테마의 상세정보를 조회합니다."
    )
    @GetMapping("/{themeId}")
    public ResponseEntity<ThemeDetailResponse> getThemeDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long themeId
    ) {
        Long memberId = userDetails != null ? userDetails.getId() : null;

        ThemeDetailResponse theme = themeService.getThemeById(memberId, themeId);

        return ResponseEntity.ok(theme);
    }
}
