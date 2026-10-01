package net.likelion.bebc25.itda.admin.reply.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.admin.reply.dto.AdminReplyResponse;
import net.likelion.bebc25.itda.admin.reply.service.AdminReplyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "관리자 댓글 API", description = "관리자 댓글 조회·삭제")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/replies")
public class AdminReplyController {

    private final AdminReplyService adminReplyService;

    @Operation(
            summary = "댓글 목록 조회",
            description = "관리자용 전체 댓글 목록을 조회한다."
    )
    @GetMapping
    public ResponseEntity<List<AdminReplyResponse>> getAllReplies() {
        List<AdminReplyResponse> replies = adminReplyService.getAllReplies();
        return ResponseEntity.ok(replies);
    }

    @Operation(
            summary = "댓글 삭제",
            description = "관리자가 댓글을 삭제한다."
    )
    @DeleteMapping("/{replyId}")
    public ResponseEntity<Void> deleteReply(@PathVariable Long replyId) {
        adminReplyService.deleteReply(replyId);
        return ResponseEntity.noContent().build();
    }
}
