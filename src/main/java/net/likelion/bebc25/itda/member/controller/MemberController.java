package net.likelion.bebc25.itda.member.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.likelion.bebc25.itda.member.dto.MemberResponse;
import net.likelion.bebc25.itda.member.dto.SignupRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Tag(name = "회원 API", description = "회원가입 기능")
@RestController
@RequestMapping("/api/v1/member")
public class MemberController {

    // 회원가입
    @PostMapping
    public ResponseEntity<MemberResponse> createPost(
            @Valid @RequestBody SignupRequest request // json 요청 바디를 객체로 자동 매핑
    ) {
        request.(userDetails.getId());

        // 게시글 등록
        PostResponse createdPost = postService.createPost(request);
        // 필수는 아님. 이거하면 restfull 해짐
        MemberResponse response =
        return ResponseEntity.ok(response);
    }

}
