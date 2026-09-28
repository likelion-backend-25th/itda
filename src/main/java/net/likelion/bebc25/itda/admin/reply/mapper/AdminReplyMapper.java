package net.likelion.bebc25.itda.admin.reply.mapper;

import net.likelion.bebc25.itda.admin.reply.dto.AdminReplyResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminReplyMapper {

    List<AdminReplyResponse> findAllReplies();

    int deleteReply(@Param("replyId") Long replyId);
}