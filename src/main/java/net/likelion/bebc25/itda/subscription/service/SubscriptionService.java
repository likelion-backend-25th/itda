package net.likelion.bebc25.itda.subscription.service;

import net.likelion.bebc25.itda.subscription.dto.MySubscriptionResponse;
import net.likelion.bebc25.itda.subscription.dto.SubscriptionRequest;

import java.util.List;

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

    List<MySubscriptionResponse> getMySubscriptions(Long memberId);

    boolean isSubscribed(
            Long memberId,
            Long targetId
    );

    void cancelSubscription(
            Long memberId,
            Long targetId
    );

    void expireSubscriptions();

    int getSubscriberCount(Long targetId);

    int getMonthlyIncome(Long targetId);
}

