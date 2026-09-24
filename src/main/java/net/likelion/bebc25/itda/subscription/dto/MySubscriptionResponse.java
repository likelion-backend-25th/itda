package net.likelion.bebc25.itda.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MySubscriptionResponse {

    private Long subscriptionId;
    private Long targetId;
    private String nickname;
    private String profileImage;
    private Long priceId;
    private LocalDateTime nextBillingAt;
    private long remainingDays;
}