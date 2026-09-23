package net.likelion.bebc25.itda.post.mapper;

import net.likelion.bebc25.itda.post.domain.Post;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper {

    // 1. 신규 게시글 등록
    void save(Post post);

    // 2. 게시글 단건 조회 (ID)
    Post findById(long id);
}
