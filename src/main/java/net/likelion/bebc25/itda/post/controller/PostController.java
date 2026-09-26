package net.likelion.bebc25.itda.post.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.member.dto.PostUpdateRequest;
import net.likelion.bebc25.itda.post.dto.PostCreateRequest;
import net.likelion.bebc25.itda.post.dto.PostFeedResponse;
import net.likelion.bebc25.itda.post.dto.PostResponse;
import net.likelion.bebc25.itda.post.service.PostService;
import net.likelion.bebc25.itda.security.principal.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    // 게시글 등록
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PostCreateRequest request)
    {
        Long memberId = userDetails.getId();
        PostResponse createdPost = postService.createPost(memberId, request);
        URI location = URI.create("/api/v1/posts/" + createdPost.id());

        return ResponseEntity.created(location).body(createdPost);
    }

    // 게시글 한 건 조회
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable("id") Long id)
    {
        // 전달받은 id를 이용해서 게시글 한 건 조회 메서드를 호출
        PostResponse post = postService.getPostById(id);
        return ResponseEntity.ok(post);
    }

    // 메인 페이지에서 게시글 피드 - 비로그인시 / 로그인시
    // userDetails - JWT 인증을 통해 현재 로그인한 회원 정보
    @GetMapping
    public ResponseEntity<PostFeedResponse> getPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) Long publicCursor,
            @RequestParam(required = false) Long subscribedCursor,
            @RequestParam(defaultValue = "10") int size
    ){
        Long memberId = null;

        // 로그인 인 유저
        if(userDetails != null) {
            memberId = userDetails.getId();
        }

        PostFeedResponse response = postService.getPosts(memberId, publicCursor, subscribedCursor, size);

        return ResponseEntity.ok(response);
    }

    // 글 수정
    @PutMapping("{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PostUpdateRequest request
    ){
        Long memberId = userDetails.getId();

        PostResponse updatedPost = postService.updatePost(memberId, id, request);

        return ResponseEntity.ok(updatedPost);
    }

    // 글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id
    ){
        postService.deletePost(id);

        return ResponseEntity.noContent().build();
    }
}
