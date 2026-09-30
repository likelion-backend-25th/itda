package net.likelion.bebc25.itda.admin.payment.dto;

import java.time.LocalDateTime;

public record AdminPaymentResponse(
        Long paymentId,
        Long memberId,
        String nickname,
        String email,
        String paymentType,
        Long targetId,
        String transactionId,
        Long amount,
        String paymentStatus,
        String paymentMethod,
        LocalDateTime paidAt,
        LocalDateTime createdAt,
        String servicePaymentId
) {
}