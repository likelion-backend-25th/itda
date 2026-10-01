package net.likelion.bebc25.itda.admin.theme.service;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.admin.theme.dto.AdminThemeRequest;
import net.likelion.bebc25.itda.admin.theme.dto.AdminThemeResponse;
import net.likelion.bebc25.itda.admin.theme.mapper.AdminThemeMapper;
import net.likelion.bebc25.itda.s3.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    public void createTheme(AdminThemeRequest request, MultipartFile themeImage) {
        if (request.cssText() == null || request.cssText().isBlank()) {
            throw new IllegalArgumentException("CSS 텍스트는 필수입니다.");
        }

        String themeCode = request.themeCode();
        if (themeCode == null || themeCode.isBlank()) {
            themeCode = newThemeCode();
        } else if (adminThemeMapper.existsByThemeCode(themeCode)) {
            throw new IllegalArgumentException("이미 사용 중인 테마 코드입니다.");
        }

        // 테마 이미지 S3 업로드
        String themeImageKey = null;

        if (themeImage != null && !themeImage.isEmpty()) {
            themeImageKey = s3Service.upload(themeImage, "themes");
        }

        adminThemeMapper.insertTheme(new AdminThemeRequest(
                request.themeName(),
                request.description(),
                request.price(),
                themeImageKey,
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
    public void updateTheme(Long themeId, AdminThemeRequest request, MultipartFile themeImage) {
        // 수정은 테마 코드를 바꾸지 않고 css_text·메타·썸네일만 갱신한다.
        if (request.cssText() == null || request.cssText().isBlank()) {
            throw new IllegalArgumentException("CSS 텍스트는 필수입니다.");
        }

        AdminThemeResponse existing = adminThemeMapper.findById(themeId);
        if (existing == null) {
            throw new IllegalArgumentException("존재하지 않는 테마입니다.");
        }

        // 새 파일이 없으면 기존 S3 키 유지 (게시글 수정과 동일)
        String oldImageKey = existing.thumbnailUrl();
        String themeImageKey = oldImageKey;
        boolean imageReplaced = false;

        if (themeImage != null && !themeImage.isEmpty()) {
            themeImageKey = s3Service.upload(themeImage, "themes");
            imageReplaced = true;
        }

        int updatedCount = adminThemeMapper.updateTheme(themeId, new AdminThemeRequest(
                request.themeName(),
                request.description(),
                request.price(),
                themeImageKey,
                existing.themeCode(),
                request.cssText()
        ));

        if (updatedCount == 0) {
            throw new IllegalArgumentException("존재하지 않는 테마입니다.");
        }

        if (imageReplaced && oldImageKey != null && !oldImageKey.isBlank() && !oldImageKey.equals(themeImageKey)) {
            s3Service.delete(oldImageKey);
        }
    }

    @Override
    @Transactional
    public void updateThemeStatus(Long themeId, String status) {
        if (!status.equals("ON_SALE") && !status.equals("HIDDEN")) {
            throw new IllegalArgumentException("올바르지 않은 테마 상태입니다.");
        }

        AdminThemeResponse existing = adminThemeMapper.findById(themeId);
        if (existing == null) {
            throw new IllegalArgumentException("존재하지 않는 테마입니다.");
        }
        // 기본 테마는 숨기면 신규 가입·기본 적용이 깨지므로 차단
        if ("HIDDEN".equals(status) && Boolean.TRUE.equals(existing.isDefault())) {
            throw new IllegalArgumentException("기본 테마는 비활성화할 수 없습니다. 다른 테마를 기본으로 지정한 뒤 다시 시도하세요.");
        }

        int updatedCount = adminThemeMapper.updateThemeStatus(themeId, status);

        if (updatedCount == 0) {
            throw new IllegalArgumentException("존재하지 않는 테마입니다.");
        }
    }

    @Override
    @Transactional
    public void setDefaultTheme(Long themeId) {
        AdminThemeResponse existing = adminThemeMapper.findById(themeId);
        if (existing == null) {
            throw new IllegalArgumentException("존재하지 않는 테마입니다.");
        }
        if (Boolean.TRUE.equals(existing.isDefault())) {
            return;
        }

        // 기본은 하나만: 기존 플래그 해제 후 대상 지정 (+ ON_SALE 강제)
        adminThemeMapper.clearDefaultThemes();
        int updatedCount = adminThemeMapper.setDefaultTheme(themeId);
        if (updatedCount == 0) {
            throw new IllegalArgumentException("존재하지 않는 테마입니다.");
        }
    }
}
