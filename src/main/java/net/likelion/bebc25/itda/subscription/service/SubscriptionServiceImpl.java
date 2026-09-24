package net.likelion.bebc25.itda.subscription.service;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.commoncode.mapper.CommonCodeMapper;
import net.likelion.bebc25.itda.domain.CommonCode;
import net.likelion.bebc25.itda.domain.Member;
import net.likelion.bebc25.itda.domain.Subscription;
import net.likelion.bebc25.itda.member.mapper.MemberMapper;
import net.likelion.bebc25.itda.subscription.dto.SubscriptionRequest;
import net.likelion.bebc25.itda.subscription.mapper.SubscriptionMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionMapper subscriptionMapper;
    private final MemberMapper memberMapper;
    private final CommonCodeMapper commonCodeMapper;

    @Override
    public void validateSubscription(
            Long memberId,
            SubscriptionRequest request
    ) {
        // 1. 자기 자신 구독 방지
        if (memberId.equals(request.getTargetId())) {
            throw new IllegalArgumentException("자기 자신은 구독할 수 없습니다.");
        }

        // 2. 구독 대상 회원 존재 여부 확인
        Member targetMember = memberMapper.findById(request.getTargetId());

        if (targetMember == null) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        // 3. 구독 가격 확인
        CommonCode price = commonCodeMapper.findActiveSubscriptionPrice(
                request.getPriceId()
        );

        if (price == null) {
            throw new IllegalArgumentException("유효하지 않은 구독 가격입니다.");
        }

    }

    @Override
    public void createSubscription(
            Long memberId,
            SubscriptionRequest request,
            String customerUid
    ) {
        // 구독 생성 전 검증
        validateSubscription(memberId, request);

        // 지금 구독중인지
        Subscription activeSubscription =
                subscriptionMapper.findActiveSubscription(
                        memberId,
                        request.getTargetId()
                );

        //이미 구독중이라면 연장
        if (activeSubscription != null) {

            subscriptionMapper.extendSubscription(
                    memberId,
                    request.getTargetId()
            );
            return;
        }

        Subscription subscription = Subscription.builder()
                .member_id(memberId)
                .target_id(request.getTargetId())
                .customer_uid(customerUid)
                .price_id(request.getPriceId())
                .build();

        subscriptionMapper.save(subscription);
    }

    @Override
    public boolean isSubscribed(
            Long memberId,
            Long targetId
    ) {
        Subscription subscription =
                subscriptionMapper.findActiveSubscription(
                        memberId,
                        targetId
                );

        return subscription != null;
    }

    @Override
    public void cancelSubscription(
            Long memberId,
            Long targetId
    ) {
        Subscription subscription =
                subscriptionMapper.findActiveSubscription(
                        memberId,
                        targetId
                );

        if (subscription == null) {
            throw new IllegalArgumentException(
                    "현재 구독 중인 회원이 아닙니다."
            );
        }

        subscriptionMapper.cancelSubscription(
                memberId,
                targetId
        );
    }
}