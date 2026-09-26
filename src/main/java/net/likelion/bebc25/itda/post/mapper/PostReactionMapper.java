package net.likelion.bebc25.itda.post.mapper;

import org.apache.ibatis.annotations.Param;

public interface PostReactionMapper {
    // 1. 좋아요 여부 확인
    int countLike(@Param("memberId") Long memberId, @Param("postId") Long postId);

    // 2. 좋아요 등록
    int insertLike(@Param("memberId") Long memberId, @Param("postId") Long postId);

    // 3. 좋아요 취소
    void deleteLike(@Param("memberId") Long memberId, @Param("postId") Long postId);
}
