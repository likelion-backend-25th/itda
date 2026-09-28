package net.likelion.bebc25.itda.reply.mapper;

import net.likelion.bebc25.itda.reply.domain.Reply;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReplyMapper {

    // 1. 댓글 등록
    void save(Reply reply);

    // 2. 댓글 조회
    Reply findById(Long id);

    // 3. 해당 게시글의 댓글 조회
    List<Reply> findByPostId(Long postId);

    // 4. 댓글 수정
    void update(Reply reply);

    // 5. 댓글 삭제
    void deleteById(Long id);
}
