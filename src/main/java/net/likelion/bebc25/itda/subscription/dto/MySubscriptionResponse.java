package net.likelion.bebc25.itda.subscription.dto;

import java.time.LocalDateTime;

public record MySubscriptionResponse(
        Long subscriptionId,
        Long targetId,
        String nickname,
        String profileImage,
        Long priceId,
        LocalDateTime nextBillingAt,
        long remainingDays
) {
}
