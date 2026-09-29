package net.likelion.bebc25.itda.admin.subscription.dto;

import java.time.LocalDateTime;

public record AdminSubscriptionResponse(
        Long subscriptionId,
        Long memberId,
        String memberNickname,
        String memberEmail,
        Long targetId,
        String targetNickname,
        String subscriptionStatus,
        Long priceId,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        LocalDateTime nextBillingAt,
        Long paymentId
) {
}