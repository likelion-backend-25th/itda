package net.likelion.bebc25.itda.admin.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.admin.member.dto.AdminMemberResponse;
import net.likelion.bebc25.itda.admin.member.service.AdminMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "관리자 회원 API", description = "관리자 회원 조회·상태 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/members")
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    @Operation(
            summary = "회원 목록 조회",
            description = "관리자용 전체 회원 목록을 조회한다."
    )
    @GetMapping
    public ResponseEntity<List<AdminMemberResponse>> getAllMembers() {
        return ResponseEntity.ok(adminMemberService.getAllMembers());
    }

    @Operation(
            summary = "회원 상태 변경",
            description = "회원 활성/비활성 상태를 변경한다."
    )
    @PatchMapping("/{memberId}/status")
    public ResponseEntity<Void> updateMemberStatus(
            @PathVariable Long memberId,
            @RequestParam String status) {
        adminMemberService.updateMemberStatus(memberId, status);
        return ResponseEntity.noContent().build();
    }
}
