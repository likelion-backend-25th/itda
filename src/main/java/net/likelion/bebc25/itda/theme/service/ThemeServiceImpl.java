package net.likelion.bebc25.itda.theme.service;

import net.likelion.bebc25.itda.dto.PageResponse;
import net.likelion.bebc25.itda.s3.S3Service;
import net.likelion.bebc25.itda.theme.dto.ThemeDetailResponse;
import net.likelion.bebc25.itda.theme.dto.ThemeResponse;
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
}