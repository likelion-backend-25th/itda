package net.likelion.bebc25.itda.post.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.post.dto.PostCreateRequest;
import net.likelion.bebc25.itda.post.dto.PostResponse;
import net.likelion.bebc25.itda.post.service.PostService;
import net.likelion.bebc25.itda.security.principal.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

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

}
