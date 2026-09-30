package net.likelion.bebc25.itda.reply.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "댓글 API", description = "게시글 댓글 CRUD")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/{postId}/replies")
public class ReplyController {
    private final ReplyService replyService;

    // 1. 댓글 등록
    @Operation(
            summary = "댓글 등록",
            description = "해당 게시글에 댓글을 등록한다."
    )
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

    // 비로그인도 허용
    // 2. 해당 게시글의 댓글들 조회
    @Operation(
            summary = "댓글 목록 조회",
            description = "게시글의 댓글 목록을 조회한다. 비로그인도 가능하다."
    )
    @GetMapping
    public ResponseEntity<List<ReplyResponse>> getReplies(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId){

        Long memberId = null;

        if (userDetails != null) {
            memberId = userDetails.getId();
        }
        List<ReplyResponse> replies = replyService.getRepliesByPostId(memberId, postId);

        return ResponseEntity.ok(replies);
    }

    // 3. 댓글 수정
    @Operation(
            summary = "댓글 수정",
            description = "본인 댓글의 내용을 수정한다."
    )
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
    @Operation(
            summary = "댓글 삭제",
            description = "본인 댓글을 삭제한다."
    )
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
