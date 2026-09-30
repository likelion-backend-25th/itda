package net.likelion.bebc25.itda.customer.payment.dto;

import java.time.LocalDateTime;

public record MyPaymentResponse(
        Long id,
        String paymentId,
        String paymentType,
        Long targetId,
        Long amount,
        String paymentStatus,
        String paymentMethod,
        LocalDateTime paidAt,
        LocalDateTime createdAt,
        Boolean refundAvailable
) {}