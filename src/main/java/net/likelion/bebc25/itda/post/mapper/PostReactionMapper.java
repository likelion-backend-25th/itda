package net.likelion.bebc25.itda.post.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

@Mapper
public interface PostReactionMapper {
    // 1. 좋아요 여부 확인
    int countLike(@Param("memberId") Long memberId, @Param("postId") Long postId);

    // 2. 좋아요 등록
    int insertLike(@Param("memberId") Long memberId, @Param("postId") Long postId);

    // 3. 좋아요 취소
    void deleteLike(@Param("memberId") Long memberId, @Param("postId") Long postId);

    // 4. 스크랩 수
    int countScrap(@Param("memberId")Long memberId, @Param("postId")Long postId);

    // 5. 스크랩 등록
    int insertScrap(@Param("memberId")Long memberId, @Param("postId")Long postId);

    // 6. 스크랩 삭제
    int deleteScrap(@Param("memberId")Long memberId, @Param("postId")Long postId);
}
