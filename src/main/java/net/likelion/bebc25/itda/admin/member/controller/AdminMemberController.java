package net.likelion.bebc25.itda.admin.member.controller;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.admin.member.dto.AdminMemberResponse;
import net.likelion.bebc25.itda.admin.member.service.AdminMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/members")
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    @GetMapping
    public ResponseEntity<List<AdminMemberResponse>> getAllMembers() {
        return ResponseEntity.ok(adminMemberService.getAllMembers());
    }

    @PatchMapping("/{memberId}/status")
    public ResponseEntity<Void> updateMemberStatus(
            @PathVariable Long memberId,
            @RequestParam String status) {
        adminMemberService.updateMemberStatus(memberId, status);
        return ResponseEntity.noContent().build();
    }
}