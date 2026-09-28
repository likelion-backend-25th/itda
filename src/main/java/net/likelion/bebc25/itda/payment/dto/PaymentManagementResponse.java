package net.likelion.bebc25.itda.payment.dto;

import java.time.LocalDateTime;

public record PaymentManagementResponse(

        // 결제 번호
        String paymentId,

        // 결제 회원
        Long memberId,

        // 결제 대상
        Long targetId,

        // 결제일
        LocalDateTime paidAt,

        // 구독 만료일 (테마 결제는 null)
        LocalDateTime endedAt,

        // 카드, 카카오페이, 토스페이 등
        String payMethod

) {
}