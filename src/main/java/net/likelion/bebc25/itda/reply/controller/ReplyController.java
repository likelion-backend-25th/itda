package net.likelion.bebc25.itda.reply.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.reply.dto.ReplyCreateRequest;
import net.likelion.bebc25.itda.reply.dto.ReplyResponse;
import net.likelion.bebc25.itda.reply.dto.ReplyUpdateRequest;
import net.likelion.bebc25.itda.reply.mapper.ReplyMapper;
import net.likelion.bebc25.itda.reply.service.ReplyService;
import net.likelion.bebc25.itda.security.principal.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/{postId}/replies")
public class ReplyController {
    private final ReplyService replyService;

    // 1. 댓글 등록
    @PostMapping
    public ResponseEntity<ReplyResponse> createReply(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ReplyCreateRequest request){

        Long memberId = userDetails.getId();

        ReplyResponse createdReply = replyService.createReply(memberId, postId, request);

        // 댓글 등록 성공시 생성된 댓글의 주소를 응답
        URI location = URI.create( "/api/v1/posts/" + postId + "/replies/" + createdReply.id());

        return ResponseEntity.created(location).body(createdReply);

    }

    // 2. 해당 게시글의 댓글들 조회
    @GetMapping
    public ResponseEntity<List<ReplyResponse>> getReplies(
            @PathVariable Long postId){
        List<ReplyResponse> replies = replyService.getRepliesByPostId(postId);

        return ResponseEntity.ok(replies);
    }

    // 3. 댓글 수정
    @PutMapping("/{replyId}")
    public ResponseEntity<ReplyResponse> updateReply(
            @PathVariable Long postId,
            @PathVariable Long replyId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ReplyUpdateRequest request){
        Long memberId = userDetails.getId();

        ReplyResponse updatedReply = replyService.updateReply(memberId, postId, replyId, request);

        return ResponseEntity.ok(updatedReply);
    }

    // 댓글 삭제
    @DeleteMapping("/{replyId}")
    public ResponseEntity<Void> deleteReply(
            @PathVariable Long postId,
            @PathVariable Long replyId,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        Long memberId = userDetails.getId();

        replyService.deleteReply(memberId, postId, replyId);

        return ResponseEntity.noContent().build();

    }


}
