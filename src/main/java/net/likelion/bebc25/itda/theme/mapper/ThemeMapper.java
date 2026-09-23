package net.likelion.bebc25.itda.theme.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ThemeMapper {

    // 테마 전체 목록 조회
    List<ThemeMapper> findAll();

    // ID 기반 게시글 단건 조회
    ThemeMapper findById(@Param("id") Long id);

    // 작성자 ID 기반 게시글 목록 조회
    List<ThemeMapper> findByMemberId(@Param("memberId") Long memberId);

//    // 신규 테마 등록 (Auto Increment ID 자동 바인딩)
//    void save(ThemeCreateRequest theme);
//
//    // 테마 본문 및 이미지 수정
//    void update(@Param("id") Long id, @Param("content") String content, @Param("imageUrl") String imageUrl);
//
//    // 게시글 단건 삭제
//    void deleteById(Long id);
//
//    // 복합 ResultMap 조인 상세 조회 (게시글 + 작성자 + 댓글 목록)
//    PostDetailResponse findPostDetailById(@Param("id") Long id);
//
//    // 동적 검색 조건 및 정렬 기반 게시글 목록 조회
//    List<PostResponse> searchPosts(PostSearchRequest condition);
//
//    // 8동적 정렬 분기 조회 (<choose>, <when>, <otherwise>)
//    List<PostResponse> findPostsWithSort(PostSearchRequest condition);
//
//    // 9. 동적 부분 수정 (<set>, <if>)
//    void updateSelective(Map<String, Object> params);
//
//    // 11. 공통 SQL 조각 재사용 조회 (<sql>, <include>)
//    PostResponse findByIdWithInclude(@Param("id") Long id);
//
//    // 다중 게시글 ID 일괄 삭제 (foreach)
//    void deleteByIds(@Param("idList") List<Long> idList);
//
//    // 게시글 좋아요 + 1
//    void increaseLikeCount(@Param("postId") Long postId);
//
//    // 게시글 좋아요 - 1
//    void decreaseLikeCount(@Param("postId") Long postId);
}
