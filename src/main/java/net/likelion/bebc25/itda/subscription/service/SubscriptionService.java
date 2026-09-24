package net.likelion.bebc25.itda.subscription.service;

import net.likelion.bebc25.itda.subscription.dto.SubscriptionRequest;

public interface SubscriptionService {

    void validateSubscription(
            Long memberId,
            SubscriptionRequest request
    );

    void createSubscription(
            Long memberId,
            SubscriptionRequest request,
            String customerUid
    );

    boolean isSubscribed(
            Long memberId,
            Long targetId
    );

    void cancelSubscription(
            Long memberId,
            Long targetId
    );
}