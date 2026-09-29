package net.likelion.bebc25.itda.mypage.service;

import net.likelion.bebc25.itda.mypage.dto.MyPagePostResponse;

public interface MyPageService {
    // 1. 내 게시글
    MyPagePostResponse getMyPosts(Long memberId, Long cursor, int size);

    // 2. 내가 좋아요한 게시글
    MyPagePostResponse getLikedPosts(Long memberId, Long cursor, int size);

    // 3. 내가 스크랩한 게시글
    MyPagePostResponse getScrappedPosts(Long memberId, Long cursor, int size);
}
