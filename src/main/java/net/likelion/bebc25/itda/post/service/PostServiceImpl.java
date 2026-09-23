package net.likelion.bebc25.itda.post.service;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.post.domain.Post;
import net.likelion.bebc25.itda.post.dto.PostCreateRequest;
import net.likelion.bebc25.itda.post.dto.PostResponse;
import net.likelion.bebc25.itda.post.mapper.PostMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor    // finale이나 @NonNull 필드들을 자동으로 생성자를 만들어준다
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;

    @Override
    @Transactional
    public PostResponse createPost(Long memberId, PostCreateRequest request) {
        Post post = Post.builder()
                .memberId(memberId)
                .categoryId(request.categoryId())
                .content(request.content())
                .imageUrl(request.imageUrl())
                .subscriberOnly(request.subscriberOnly())
                .build();

        postMapper.save(post);

        return PostResponse.from(post);
    }

    @Override
    public PostResponse getPostById(Long id) {
        Post post = postMapper.findById(id);
        if (post == null) {
            throw new NoSuchElementException("존재하지 않는 게시글입니다. ID: " + id);
        }
        return PostResponse.from(post);
    }
}
