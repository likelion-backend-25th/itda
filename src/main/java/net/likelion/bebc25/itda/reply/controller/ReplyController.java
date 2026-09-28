package net.likelion.bebc25.itda.reply.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.reply.dto.ReplyCreateRequest;
import net.likelion.bebc25.itda.reply.dto.ReplyResponse;
import net.likelion.bebc25.itda.reply.mapper.ReplyMapper;
import net.likelion.bebc25.itda.reply.service.ReplyService;
import net.likelion.bebc25.itda.security.principal.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/{postId}/replies")
public class ReplyController {
    private final ReplyService replyService;

    // 댓글 등록
    @PostMapping
    public ResponseEntity<ReplyResponse> createReply(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ReplyCreateRequest request){

        Long memberId = userDetails.getId();

        ReplyResponse createdReply = replyService.createReply(memberId, postId, request);

        URI location = URI.create( "/api/v1/posts/" + postId + "/replies/" + createdReply.id());

        return ResponseEntity.created(location).body(createdReply);

    }
}
