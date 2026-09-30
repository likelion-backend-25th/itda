package net.likelion.bebc25.itda.mypage.controller;

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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mypage")
public class MyPageController {

    private final MyPageService myPageService;

    // 내가 쓴 게시글
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
