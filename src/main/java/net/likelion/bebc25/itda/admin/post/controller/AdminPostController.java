package net.likelion.bebc25.itda.admin.post.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.admin.post.dto.AdminPostResponse;
import net.likelion.bebc25.itda.admin.post.service.AdminPostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "관리자 게시글 API", description = "관리자 게시글 관리 기능")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/posts")
public class AdminPostController {

    private final AdminPostService adminPostService;

    @GetMapping
    public ResponseEntity<List<AdminPostResponse>> getAllPosts() {

        List<AdminPostResponse> posts =
                adminPostService.getAllPosts();

        return ResponseEntity.ok(posts);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId
    ) {
        adminPostService.deletePost(postId);

        return ResponseEntity.noContent().build();
    }
}