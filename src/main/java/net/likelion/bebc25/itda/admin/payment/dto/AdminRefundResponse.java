package net.likelion.bebc25.itda.admin.payment.dto;

import java.time.LocalDateTime;

public record AdminRefundResponse(
        Long id,
        Long paymentId,
        String cancellationId,
        String refundReason,
        Long refundAmount,
        Long deductionAmount,
        LocalDateTime requestedAt,
        LocalDateTime refundedAt
) {
}
