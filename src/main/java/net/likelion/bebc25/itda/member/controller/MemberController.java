package net.likelion.bebc25.itda.member.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.likelion.bebc25.itda.domain.Member;
import net.likelion.bebc25.itda.member.dto.MemberProfileResponse;
import net.likelion.bebc25.itda.member.dto.MemberResponse;
import net.likelion.bebc25.itda.member.dto.SignupRequest;
import net.likelion.bebc25.itda.security.principal.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "회원 API", description = "회원가입 기능")
@RestController
@RequestMapping("/api/v1/member")
public class MemberController {

    // 마이 페이지
    @GetMapping("/me")
    public ResponseEntity<MemberProfileResponse> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails //SecurityContext에서 인증된 사용자 정보를 반환한다.
    ) {
        // 인증된 사용자 정보를 꺼내서 반환한다.
        Member member = userDetails.getMember();
        return ResponseEntity.ok(MemberProfileResponse.from(member));
    }

}
