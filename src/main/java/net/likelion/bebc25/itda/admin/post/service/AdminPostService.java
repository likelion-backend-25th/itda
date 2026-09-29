package net.likelion.bebc25.itda.admin.post.service;

import net.likelion.bebc25.itda.admin.post.dto.AdminPostResponse;

import java.util.List;

public interface AdminPostService {

    List<AdminPostResponse> getAllPosts();

    void deletePost(Long postId);
}