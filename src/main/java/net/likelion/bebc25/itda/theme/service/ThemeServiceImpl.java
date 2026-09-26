package net.likelion.bebc25.itda.theme.service;

import net.likelion.bebc25.itda.dto.PageResponse;
import net.likelion.bebc25.itda.post.domain.Post;
import net.likelion.bebc25.itda.post.dto.PostResponse;
import net.likelion.bebc25.itda.theme.dto.ThemeDetailResponse;
import net.likelion.bebc25.itda.theme.dto.ThemeResponse;
import net.likelion.bebc25.itda.theme.mapper.ThemeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class ThemeServiceImpl implements ThemeService{

    private final ThemeMapper themeMapper;

    public ThemeServiceImpl(ThemeMapper themeMapper) {
        this.themeMapper = themeMapper;
    }

    @Override
    public PageResponse<ThemeResponse> getAllThemesById(Long memberId, int page, int size) {
        if(page < 1) page = 1;
        if(size < 1) size = 6;

        int offset = (page - 1) * size;
        List<ThemeResponse> content = themeMapper.findAllThemes(memberId,size,offset);
        long total = themeMapper.countThemes();

        int totalPages = (int) Math.ceil((double) total / size);

        return new PageResponse<>(content, page, size, total, totalPages);
    }

    @Override
    public ThemeDetailResponse getThemeById(Long id) {
//        Post post = postMapper.findById(id);
//        if (post == null) {
//            throw new NoSuchElementException("존재하지 않는 게시글입니다. ID: " + id);
//        }
//        return PostResponse.from(post);

        return null;
    }
}