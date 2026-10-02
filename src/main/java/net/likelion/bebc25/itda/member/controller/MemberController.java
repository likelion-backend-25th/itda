package net.likelion.bebc25.itda.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.domain.Member;
import net.likelion.bebc25.itda.member.dto.*;
import net.likelion.bebc25.itda.member.service.FollowService;
import net.likelion.bebc25.itda.member.service.MemberService;
import net.likelion.bebc25.itda.security.principal.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "회원 API", description = "회원 기능")
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
    @Operation(
            summary = "팔로워 목록 조회",
            description = "특정 회원의 팔로워 목록을 조회한다."
    )
    @GetMapping("/{id}/followers")
    public ResponseEntity<List<FollowerResponse>> getFollowers(
            @PathVariable Long id
    ) {
        List<FollowerResponse> followers = memberService.getFollowers(id);

        return ResponseEntity.ok(followers);
    }

    // 특정 회원의 팔로잉 목록 조회
    @Operation(
            summary = "팔로잉 목록 조회",
            description = "특정 회원이 팔로우 중인 목록을 조회한다."
    )
    @GetMapping("/{id}/followings")
    public ResponseEntity<List<FollowingResponse>> getFollowings(
            @PathVariable Long id
    ) {
        List<FollowingResponse> followings = memberService.getFollowings(id);

        return ResponseEntity.ok(followings);
    }

    // 회원 팔로우
    @Operation(
            summary = "회원 팔로우",
            description = "특정 회원을 팔로우한다."
    )
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
    @Operation(
            summary = "회원 언팔로우",
            description = "특정 회원 팔로우를 해제한다."
    )
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
    @Operation(
            summary = "내 프로필 수정",
            description = """
                    multipart/form-data로 내 프로필을 수정한다.
                    - request: MemberUpdateRequest JSON (nickname, introduction, removeProfileImage)
                    - profileImage: 프로필 이미지 파일 (선택). 파일이 있으면 교체 우선, removeProfileImage는 무시된다.
                    """
    )
    @ApiResponse(responseCode = "204", description = "수정 성공 (Body 없음)")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    schema = @Schema(implementation = ProfileUpdateMultipart.class),
                    encoding = {
                            @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE)
                    }
            )
    )
    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(hidden = true)
            @Valid @RequestPart("request") MemberUpdateRequest request,
            @Parameter(hidden = true)
            @RequestPart(value = "profileImage", required = false)
            MultipartFile profileImage
    ) {
        Long memberId = userDetails.getMember().getId();

        memberService.updateMyProfile(memberId, request, profileImage);

        return ResponseEntity.noContent().build();
    }

    // 내 관심사 수정 (전체 교체)
    @Operation(
            summary = "내 관심사 수정",
            description = "내 관심사 목록을 전체 교체한다."
    )
    @PutMapping("/me/interests")
    public ResponseEntity<Void> updateMyInterests(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody InterestUpdateRequest request
    ) {
        Long memberId = userDetails.getMember().getId();
        memberService.updateMyInterests(memberId, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Swagger multipart 스키마용.
     * 실제 바인딩은 @RequestPart("request") + @RequestPart("profileImage")를 사용한다.
     */
    @Schema(name = "ProfileUpdateMultipart", description = "프로필 수정 multipart form")
    public static class ProfileUpdateMultipart {

        @Schema(
                description = "프로필 수정 JSON (part name: request)",
                implementation = MemberUpdateRequest.class,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        public MemberUpdateRequest request;

        @Schema(
                description = "프로필 이미지 파일 (part name: profileImage, 선택)",
                type = "string",
                format = "binary"
        )
        public MultipartFile profileImage;
    }
}
