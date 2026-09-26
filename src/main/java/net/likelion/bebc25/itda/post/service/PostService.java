package net.likelion.bebc25.itda.post.service;

import net.likelion.bebc25.itda.member.dto.PostUpdateRequest;
import net.likelion.bebc25.itda.post.dto.PostCreateRequest;
import net.likelion.bebc25.itda.post.dto.PostFeedResponse;
import net.likelion.bebc25.itda.post.dto.PostResponse;

public interface PostService {

    // 1. 게시글 신규 등록
    PostResponse createPost(Long memberId, PostCreateRequest request);

    // 2. 게시글 단건 조회
    PostResponse getPostById(Long id);

    // 3. 게시글 피드 무한 스크롤
    PostFeedResponse getPosts(Long memberId, Long publicCursor, Long subscribedCursor, int size);

    // 4. 게시글 수정
    PostResponse updatePost(Long memberId, Long postId, PostUpdateRequest request);

    // 5. 게시글 삭제
    void  deletePost(Long postId);

}
