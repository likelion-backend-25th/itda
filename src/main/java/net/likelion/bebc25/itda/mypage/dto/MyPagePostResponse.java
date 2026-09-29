package net.likelion.bebc25.itda.mypage.dto;

import net.likelion.bebc25.itda.post.dto.PostResponse;

import java.util.List;

public record  MyPagePostResponse (
        List<PostResponse> posts,
        Long nextCursor,
        boolean hadNext
){ }
