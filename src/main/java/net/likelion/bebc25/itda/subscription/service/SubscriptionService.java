package net.likelion.bebc25.itda.subscription.service;

import net.likelion.bebc25.itda.dto.PageResponse;
import net.likelion.bebc25.itda.subscription.dto.MySubscriptionResponse;
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

    PageResponse<MySubscriptionResponse> getMySubscriptions(Long memberId, int page, int size);

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

    void deleteSubscription(Long memberId, Long targetId);
}

