package net.likelion.bebc25.itda.post.mapper;

import net.likelion.bebc25.itda.post.domain.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {

    // 1. 신규 게시글 등록
    void save(Post post);

    // 2. 게시글 단건 조회 (ID)
    Post findById(long id);

    // 3. 비로그인 시 보이는 메인 게시글(구독자 전용 게시글을 제외한 모든 게시글만 보임)
    List<Post> findPublicPostsByCursor(@Param("cursor") Long cursor, @Param("size") int size);

    // 3.1 로그인 시 보이는 메인 게시글(구독자 전용 게시글 + 일반 게시글, size = 게시글수)
    List<Post> findSubscribedPostsByCursor(@Param("memberId")Long memberId,
                                       @Param("cursor") Long cursor,
                                       @Param("size")int size);


    // 4. 게시글 수정
    void update(Post post);

    // 5. 게시글 삭제
    void deleteById(long id);
}
