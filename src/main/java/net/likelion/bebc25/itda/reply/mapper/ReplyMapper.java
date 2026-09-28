package net.likelion.bebc25.itda.reply.mapper;

import net.likelion.bebc25.itda.reply.domain.Reply;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReplyMapper {

    // 1. 댓글 등록
    void save(Reply reply);

    // 2. 댓글 조회
    Reply findById(Long id);
}
