package net.likelion.bebc25.itda.admin.reply.controller;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.admin.reply.dto.AdminReplyResponse;
import net.likelion.bebc25.itda.admin.reply.service.AdminReplyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/replies")
public class AdminReplyController {

    private final AdminReplyService adminReplyService;

    @GetMapping
    public ResponseEntity<List<AdminReplyResponse>> getAllReplies() {
        List<AdminReplyResponse> replies = adminReplyService.getAllReplies();
        return ResponseEntity.ok(replies);
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<Void> deleteReply(@PathVariable Long replyId) {
        adminReplyService.deleteReply(replyId);
        return ResponseEntity.noContent().build();
    }
}