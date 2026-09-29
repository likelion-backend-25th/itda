package net.likelion.bebc25.itda.theme.service;

import net.likelion.bebc25.itda.dto.PageResponse;
import net.likelion.bebc25.itda.s3.S3Service;
import net.likelion.bebc25.itda.theme.dto.ThemeDetailResponse;
import net.likelion.bebc25.itda.theme.dto.ThemeResponse;
import net.likelion.bebc25.itda.theme.dto.ThemeStylesResponse;
import net.likelion.bebc25.itda.theme.mapper.ThemeMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class ThemeServiceImpl implements ThemeService{

    private final ThemeMapper themeMapper;
    private final S3Service s3Service;

    public ThemeServiceImpl(ThemeMapper themeMapper, S3Service s3Service) {
        this.themeMapper = themeMapper;
        this.s3Service = s3Service;
    }

    @Override
    public Long getDefaultThemeId(){
        Long themeId = themeMapper.findDefaultThemeId();

        return themeId;
    }

    @Override
    public PageResponse<ThemeResponse> getAllThemes(Long memberId, int page, int size) {
        if(page < 1) page = 1;
        if(size < 1) size = 6;

        int offset = (page - 1) * size;
        List<ThemeResponse> content = themeMapper.findAllThemes(memberId,size,offset);
        List<ThemeResponse> responses = content.stream()
                .map(theme -> ThemeResponse.from(
                        theme,
                        s3Service.getPresignedUrl(theme.thumbnailUrl())
                ))
                .toList();

        long total = themeMapper.countThemes();

        return PageResponse.of(responses, page, size, total);
    }

    @Override
    public ThemeDetailResponse getThemeById(Long memberId, Long themeId) {
        ThemeDetailResponse theme = themeMapper.findById(memberId, themeId);

        if (theme == null) {
            throw new NoSuchElementException("존재하지 않는 테마입니다. ID: " + themeId);
        }

        return ThemeDetailResponse.from(theme, s3Service.getPresignedUrl(theme.thumbnailUrl()));
    }

    @Override
    public void insertDefaultTheme(Long memberId, Long themeId) {

        themeMapper.insertDefaultTheme(memberId, themeId);
    }

    @Override
    public PageResponse<ThemeResponse> getOwnedThemes(Long memberId, int page, int size) {
        if(page < 1) page = 1;
        if(size < 1) size = 6;

        int offset = (page - 1) * size;

        List<ThemeResponse> themes = themeMapper.findOwnedThemes(memberId, size, offset);
        List<ThemeResponse> responses = themes.stream()
                .map(theme -> ThemeResponse.from(
                        theme,
                        s3Service.getPresignedUrl(theme.thumbnailUrl())
                ))
                .toList();

        long totalCount = themeMapper.countOwnedThemes(memberId);

        return PageResponse.of(responses, page, size, totalCount);
    }

    @Override
    public ThemeStylesResponse getThemeStyles(Long memberId, Long themeId) {
        if (memberId == null) {
            // GlobalRestExceptionHandler: IllegalStateException → 403
            // 미인증은 Security가 401로 막는 게 정석. 방어 코드.
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        ThemeStylesResponse.ThemeStyleRow row = themeMapper.findStyleById(themeId);
        if (row == null) {
            throw new NoSuchElementException("존재하지 않는 테마입니다. ID: " + themeId);
        }

        boolean owned = Boolean.TRUE.equals(row.isDefault()) || themeMapper.existsPurchase(memberId, themeId);
        if (!owned) {
            throw new IllegalStateException("보유하지 않은 테마입니다.");
        }

        if (row.cssText() == null || row.cssText().isBlank()) {
            throw new NoSuchElementException("테마 스타일이 등록되지 않았습니다. ID: " + themeId);
        }

        return ThemeStylesResponse.of(row);
    }

    @Override
    @Transactional
    public ThemeDetailResponse applyTheme(Long memberId, Long themeId) {
        if (memberId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        ThemeStylesResponse.ThemeStyleRow row = themeMapper.findStyleById(themeId);

        if (row == null) {
            throw new NoSuchElementException("존재하지 않는 테마입니다. ID: " + themeId);
        }

        boolean owned = Boolean.TRUE.equals(row.isDefault()) || themeMapper.existsPurchase(memberId, themeId);
        if (!owned) {
            throw new IllegalStateException("보유하지 않은 테마입니다.");
        }

        // 1) 현재 적용 테마만 교체
        themeMapper.updateMemberThemeId(memberId, themeId);
        // 2) 구매 이력이 있으면 "사용함" 플래그 ON (이미 true면 그대로)
        //    다른 테마 is_used 는 절대 초기지 않음
        if (themeMapper.existsPurchase(memberId, themeId)) {
            themeMapper.markThemeUsedIfNeeded(memberId, themeId);
        }
        return getThemeById(memberId, themeId);
    }

    @Override
    @Transactional
    public ThemeDetailResponse claimFreeTheme(Long memberId, Long themeId) {
        if (memberId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        Integer price = themeMapper.findPriceById(themeId);
        String status = themeMapper.findStatusById(themeId);
        if (price == null || status == null) {
            throw new NoSuchElementException("존재하지 않는 테마입니다. ID: " + themeId);
        }
        if (!"ON_SALE".equals(status)) {
            throw new IllegalStateException("판매 중이 아닌 테마입니다.");
        }
        if (price > 0) {
            throw new IllegalArgumentException("유료 테마는 PortOne 결제가 필요합니다.");
        }

        // 이미 보유면 그대로 상세 반환 (멱등)
        if (!themeMapper.existsPurchase(memberId, themeId)) {
            themeMapper.insertFreeThemePurchase(memberId, themeId);
        }
        return getThemeById(memberId, themeId);
    }
}