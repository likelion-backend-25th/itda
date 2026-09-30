package net.likelion.bebc25.itda.mypage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.mypage.dto.MyPagePostResponse;
import net.likelion.bebc25.itda.mypage.service.MyPageService;
import net.likelion.bebc25.itda.security.principal.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "마이페이지 API", description = "내 게시글·좋아요·스크랩 조회")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mypage")
public class MyPageController {

    private final MyPageService myPageService;

    // 내가 쓴 게시글
    @Operation(
            summary = "내가 쓴 게시글 조회",
            description = "로그인한 회원이 작성한 게시글을 커서 기반으로 조회한다."
    )
    @GetMapping("/posts")
    public ResponseEntity<MyPagePostResponse> getMyPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "5") int size){
        Long memberId = userDetails.getId();

        MyPagePostResponse response = myPageService.getMyPosts(memberId, cursor, size);

        return ResponseEntity.ok(response);
    }

    // 좋아요한 게시글
    @Operation(
            summary = "좋아요한 게시글 조회",
            description = "로그인한 회원이 좋아요한 게시글을 커서 기반으로 조회한다."
    )
    @GetMapping("/likes")
    public ResponseEntity<MyPagePostResponse> getLikedPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "5") int size){
        Long memberId = userDetails.getId();

        MyPagePostResponse response = myPageService.getLikedPosts(memberId, cursor, size);

        return ResponseEntity.ok(response);

    }
    // 스크랩한 게시글
    @Operation(
            summary = "스크랩한 게시글 조회",
            description = "로그인한 회원이 스크랩한 게시글을 커서 기반으로 조회한다."
    )
    @GetMapping("/scraps")
    public ResponseEntity<MyPagePostResponse> getScrappedPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "5") int size){
        Long memberId = userDetails.getId();

        MyPagePostResponse response = myPageService.getScrappedPosts(memberId, cursor, size);

        return ResponseEntity.ok(response);
    }
}
