package net.likelion.bebc25.itda.post.dto;

import java.util.List;

public record PostFeedResponse(
        // 이번 요청에서 가져온 게시글 목록
        List<PostResponse> posts,
        // 다음 공개 게시글 조회 시작점
        Long nextPublicCursor,
        // 다음 구독 전용  게시글 조회 시작점
        Long nextSubscribedCursor,
        // 다음에 더 가져올 게시글이 있는지 여부
        boolean hasNext
) { }
