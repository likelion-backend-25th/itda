package net.likelion.bebc25.itda.mypage.dto;

import net.likelion.bebc25.itda.post.dto.PostResponse;

import java.util.List;

public record MyPagePostResponse (
        // 이번에 조회된 게시글 목록
        List<PostResponse> posts,
        // 다음 요청에 사용할 마지막 게시글 id
        Long nextCursor,
        // 다음 데이터가 더 있는지 여부
        boolean hadNext
){ }
