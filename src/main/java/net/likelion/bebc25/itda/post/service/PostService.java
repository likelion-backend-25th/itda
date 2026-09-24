package net.likelion.bebc25.itda.post.service;

import net.likelion.bebc25.itda.post.dto.PostCreateRequest;
import net.likelion.bebc25.itda.post.dto.PostResponse;

public interface PostService {

    // 게시글 신규 등록
    PostResponse createPost(Long memberId, PostCreateRequest request);

    // 게시글 단건 조회
    PostResponse getPostById(Long id);

}
