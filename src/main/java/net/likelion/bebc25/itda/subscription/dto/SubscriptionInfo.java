package net.likelion.bebc25.itda.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

// 내가 구독한 정보
@Getter
@AllArgsConstructor
public class SubscriptionInfo {

    private Long subscriptionId;
    private Long targetId;
    private String nickname;
    private String profileImage;
    private Long priceId;
    private LocalDateTime nextBillingAt;
}