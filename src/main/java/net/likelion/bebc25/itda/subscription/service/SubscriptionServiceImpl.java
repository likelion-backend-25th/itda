package net.likelion.bebc25.itda.subscription.service;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.commoncode.mapper.CommonCodeMapper;
import net.likelion.bebc25.itda.domain.CommonCode;
import net.likelion.bebc25.itda.domain.Member;
import net.likelion.bebc25.itda.domain.Subscription;
import net.likelion.bebc25.itda.member.mapper.MemberMapper;
import net.likelion.bebc25.itda.s3.S3Service;
import net.likelion.bebc25.itda.subscription.dto.MySubscriptionResponse;
import net.likelion.bebc25.itda.subscription.dto.SubscriptionInfo;
import net.likelion.bebc25.itda.subscription.dto.SubscriptionRequest;
import net.likelion.bebc25.itda.subscription.mapper.SubscriptionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionMapper subscriptionMapper;
    private final MemberMapper memberMapper;
    private final CommonCodeMapper commonCodeMapper;
    private final S3Service s3Service;

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
    @Transactional
    public void createSubscription(
            Long memberId,
            SubscriptionRequest request,
            String customerUid
    ) {
        // 구독 생성 전 검증
        validateSubscription(memberId, request);

        // 현재 구독 중인지 확인
        Subscription activeSubscription =
                subscriptionMapper.findActiveSubscription(
                        memberId,
                        request.getTargetId()
                );

        // 이미 구독 중이라면 연장
        if (activeSubscription != null) {
            subscriptionMapper.extendSubscription(
                    memberId,
                    request.getTargetId()
            );
            return;
        }

        // 신규 구독 생성
        Subscription subscription = Subscription.builder()
                .member_id(memberId)
                .target_id(request.getTargetId())
                .billing_key(null)
                .price_id(request.getPriceId())
                .payment_id(null)
                .build();

        subscriptionMapper.save(subscription);
    }

    @Override
    public List<MySubscriptionResponse> getMySubscriptions(Long memberId) {

        List<SubscriptionInfo> subscriptions =
                subscriptionMapper.findMySubscriptions(memberId);

        LocalDateTime now = LocalDateTime.now();

        return subscriptions.stream()
                .map(subscription -> {

                    // 결제 완료로 만든 구독은 next_billing_at 이 비어 있을 수 있다. 그때도 목록은 반환한다.
                    LocalDateTime nextBillingAt = subscription.getNextBillingAt();
                    long remainingDays = nextBillingAt == null
                            ? 0
                            : Math.max(ChronoUnit.DAYS.between(now, nextBillingAt), 0);

                    // DB에는 S3 key가 있으므로 presigned URL로 변환
                    return new MySubscriptionResponse(
                            subscription.getSubscriptionId(),
                            subscription.getTargetId(),
                            subscription.getNickname(),
                            s3Service.getPresignedUrl(subscription.getProfileImage()),
                            subscription.getPriceId(),
                            nextBillingAt,
                            remainingDays
                    );
                })
                .toList();
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
    @Transactional
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

    @Override
    @Transactional
    public void expireSubscriptions() {
        subscriptionMapper.expireSubscriptions();
    }

    @Override
    public int getSubscriberCount(Long targetId) {
        return subscriptionMapper.countActiveSubscribers(targetId);
    }

    @Override
    @Transactional
    public int getMonthlyIncome(Long targetId) {
        // 이번 달 구독 수익을 계산
        int monthlyIncome = subscriptionMapper.getMonthlyIncome(targetId);
        // 계산된 수익을 member.month_income에 저장
        memberMapper.updateMonthlyIncome(targetId, monthlyIncome);
        // 계산된 수익 반환
        return monthlyIncome;
    }

    @Override
    @Transactional
    public void deleteSubscription(Long memberId, Long targetId) {
        int result = subscriptionMapper.deleteSubscription(memberId, targetId);

        if (result == 0) {
            throw new IllegalArgumentException("삭제할 구독 내역이 없습니다.");
        }
    }
}