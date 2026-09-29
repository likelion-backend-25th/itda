package net.likelion.bebc25.itda.post.service;

import net.likelion.bebc25.itda.member.dto.PostUpdateRequest;
import net.likelion.bebc25.itda.post.domain.Post;
import net.likelion.bebc25.itda.post.dto.PostCreateRequest;
import net.likelion.bebc25.itda.post.dto.PostFeedResponse;
import net.likelion.bebc25.itda.post.dto.PostResponse;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {

    // 1. 게시글 신규 등록
    PostResponse createPost(Long memberId, PostCreateRequest request, MultipartFile postImage);

    // 2. 게시글 단건 조회 & 쿠키로 조회수
    PostResponse getPostById(Long id, Long memberId, boolean alreadyViewed);

    // 3. 게시글 피드 무한 스크롤
    PostFeedResponse getPosts(Long memberId, Long publicCursor, Long subscribedCursor, Long categoryId, int size);

    // 4. 게시글 수정
    PostResponse updatePost(Long memberId, Long postId, PostUpdateRequest request, MultipartFile postImage);

    // 5. 게시글 삭제
    void  deletePost(Long postId);

    // 6. 검증된 접근
    void validatePostAccess(Post post, Long memberId);

}
