package net.likelion.bebc25.itda.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.domain.Member;
import net.likelion.bebc25.itda.member.dto.*;
import net.likelion.bebc25.itda.member.service.FollowService;
import net.likelion.bebc25.itda.member.service.MemberService;
import net.likelion.bebc25.itda.security.principal.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "회원 API", description = "회원가입 기능")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;
    private final FollowService followService;

    // 회원가입
    @Operation(
            summary = "회원가입",
            description = "회원정보를 입력하여 회원가입을 한다."
    )
    @PostMapping
    public ResponseEntity<Void> signup(
            @Valid @RequestPart("request") SignupRequest request,
            @RequestPart(value = "profileImage", required = false)
            MultipartFile profileImage
    ) {
        memberService.signup(request, profileImage);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 내 프로필 정보 조회
    @Operation(
            summary = "내 프로필 정보 조회",
            description = "나의 프로필 정보를 조회한다."
    )
    @GetMapping("/me")
    public ResponseEntity<MemberProfileResponse> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails //SecurityContext에서 인증된 사용자 정보를 반환한다.
    ) {
        Member member = userDetails.getMember();
        MemberProfileResponse profile = memberService.getProfile(member);

        return ResponseEntity.ok(profile);
    }

    // 프로필 정보 조회
    @Operation(
            summary = "프로필 정보 조회",
            description = "id에 해당하는 사용자의 프로필 정보를 조회한다."
    )
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberProfileResponse> getProfile(@PathVariable Long memberId) {
        Member member = memberService.findById(memberId);

        return ResponseEntity.ok(memberService.getProfile(member));
    }

    // 특정 회원의 팔로워 목록 조회
    @GetMapping("/{id}/followers")
    public ResponseEntity<List<FollowerResponse>> getFollowers(
            @PathVariable Long id
    ) {
        List<FollowerResponse> followers = memberService.getFollowers(id);

        return ResponseEntity.ok(followers);
    }

    // 특정 회원의 팔로잉 목록 조회
    @GetMapping("/{id}/followings")
    public ResponseEntity<List<FollowingResponse>> getFollowings(
            @PathVariable Long id
    ) {
        List<FollowingResponse> followings = memberService.getFollowings(id);

        return ResponseEntity.ok(followings);
    }

    // 회원 팔로우
    @PostMapping("/{id}/follow")
    public ResponseEntity<Void> follow(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long fromId = userDetails.getMember().getId();

        followService.follow(fromId, id);

        return ResponseEntity.ok().build();
    }

    // 회원 언팔로우
    @DeleteMapping("/{id}/follow")
    public ResponseEntity<Void> unfollow(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long fromId = userDetails.getMember().getId();

        followService.unfollow(fromId, id);

        return ResponseEntity.ok().build();
    }

    // 회원 정보 수정 — signup과 동일하게 multipart (request JSON + profileImage)
    @PutMapping("/me")
    public ResponseEntity<Void> updateMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestPart("request") MemberUpdateRequest request,
            @RequestPart(value = "profileImage", required = false)
            MultipartFile profileImage
    ) {
        Long memberId = userDetails.getMember().getId();

        memberService.updateMyProfile(memberId, request, profileImage);

        return ResponseEntity.noContent().build();
    }

    // 내 관심사 수정 (전체 교체)
    @PutMapping("/me/interests")
    public ResponseEntity<Void> updateMyInterests(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody InterestUpdateRequest request
    ) {
        Long memberId = userDetails.getMember().getId();
        memberService.updateMyInterests(memberId, request);
        return ResponseEntity.noContent().build();
    }
}
