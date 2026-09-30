package net.likelion.bebc25.itda.admin.post.service;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.admin.post.dto.AdminPostResponse;
import net.likelion.bebc25.itda.admin.post.mapper.AdminPostMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminPostServiceImpl implements AdminPostService {

    private final AdminPostMapper adminPostMapper;

    @Override
    public List<AdminPostResponse> getAllPosts() {
        return adminPostMapper.findAllPosts();
    }

    @Transactional
    @Override
    public void deletePost(Long postId) {

        int deletedCount =
                adminPostMapper.deletePost(postId);

        if (deletedCount == 0) {
            throw new IllegalArgumentException(
                    "존재하지 않는 게시글입니다."
            );
        }
    }
}