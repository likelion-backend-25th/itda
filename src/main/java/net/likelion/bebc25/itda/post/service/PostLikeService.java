package net.likelion.bebc25.itda.post.service;

import net.likelion.bebc25.itda.post.dto.PostLikeResponse;

public interface PostLikeService {
    PostLikeResponse toggleLike(Long memberId, Long postId);
}
