package net.likelion.bebc25.itda.admin.post.mapper;

import net.likelion.bebc25.itda.admin.post.dto.AdminPostResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminPostMapper {

    List<AdminPostResponse> findAllPosts();

    int deletePost(@Param("postId") Long postId);
}