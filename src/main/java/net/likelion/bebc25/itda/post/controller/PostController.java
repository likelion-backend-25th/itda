package net.likelion.bebc25.itda.post.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.member.dto.PostUpdateRequest;
import net.likelion.bebc25.itda.post.dto.*;
import net.likelion.bebc25.itda.post.service.PostLikeService;
import net.likelion.bebc25.itda.post.service.PostScrapService;
import net.likelion.bebc25.itda.post.service.PostService;
import net.likelion.bebc25.itda.security.principal.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;
    private final PostLikeService postLikeService;
    private final PostScrapService postScrapService;
    private final View view;

    // 1. 게시글 등록
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

    // 2. 게시글 한 건 조회
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @CookieValue(name = "viewedPosts", required = false) String viewedPosts,
            HttpServletResponse response) {

        // 이미 조회한 게시글인지 확인
        // true / false(조회한 게시글이 아님)
        boolean alreadyViewed = hasViewedPosts(viewedPosts, id);

        // 비로그인시
        Long memberId = null;

        // 로그인시
        if(userDetails != null) {
            memberId = userDetails.getId();
        }

        // 전달받은 id를 이용해서 게시글 한 건 조회 메서드를 호출
        PostResponse post = postService.getPostById(id, memberId, alreadyViewed);

        // 처음 조회한 게시글이면 쿠키 추가
        if (!alreadyViewed) {
            addViewedPostCookie(viewedPosts, id, response);
        }
        return ResponseEntity.ok(post);
    }


    // 메인 페이지에서 게시글 피드 - 비로그인시 / 로그인시
    // userDetails - JWT 인증을 통해 현재 로그인한 회원 정보
    @GetMapping
    public ResponseEntity<PostFeedResponse> getPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) Long publicCursor,
            @RequestParam(required = false) Long subscribedCursor,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "10") int size
    ){
        Long memberId = null;

        // 로그인 인 유저
        if(userDetails != null) {
            memberId = userDetails.getId();
        }

        PostFeedResponse response = postService.getPosts(memberId, publicCursor, subscribedCursor, categoryId, size);

        return ResponseEntity.ok(response);
    }

    // 3. 글 수정
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

    // 4. 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id
    ) {

        postService.deletePost(id);

        return ResponseEntity.noContent().build();
    }

    // 5. 게시글 좋아요 토글
    @PostMapping("/{id}/like")
    public ResponseEntity<PostLikeResponse> toggleLike(
            @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getId();

        PostLikeResponse response = postLikeService.toggleLike(memberId, id);

        return ResponseEntity.ok(response);
    }

    // 6. 게시글 스크랩 토글
    @PostMapping("/{id}/scrap")
    public ResponseEntity<PostScrapResponse> toggleScrap(
            @PathVariable("id") Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        Long memberId = userDetails.getId();

        PostScrapResponse response = postScrapService.toggleScrap(memberId, postId);

        return ResponseEntity.ok(response);
    }

    // 7. 쿠키에 해당 게시글 ID가 있는지 확인
    private boolean hasViewedPosts(String viewedPosts, Long postId) {

        // viewedPosts 쿠키가 없거나 viewedPosts쿠키의 값이 비어있으면
        if(viewedPosts == null || viewedPosts.isBlank()) {
           return false;
        }

        return viewedPosts.contains("[" + postId + "]");
    }

    // 8. 쿠키 생성
    private void addViewedPostCookie(
            String viewedPosts,
            Long postId,
            HttpServletResponse response
    ) {

        String newViewedPosts;

        if (viewedPosts == null || viewedPosts.isBlank()) {
            newViewedPosts = "[" + postId + "]";
        } else {
            newViewedPosts = viewedPosts + "[" + postId + "]";
        }

        Cookie cookie = new Cookie(
                "viewedPosts",
                newViewedPosts
        );

        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24); // 24시간
        // 쿠키삭제 - 쿠키로 조회 테스트용
//        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }

}


