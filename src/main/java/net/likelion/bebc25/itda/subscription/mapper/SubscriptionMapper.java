package net.likelion.bebc25.itda.subscription.mapper;

import net.likelion.bebc25.itda.domain.Subscription;
import net.likelion.bebc25.itda.subscription.dto.MySubscriptionResponse;
import net.likelion.bebc25.itda.subscription.dto.SubscriptionInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SubscriptionMapper {

    Subscription findActiveSubscription(
            @Param("memberId") Long memberId,
            @Param("targetId") Long targetId
    );

    int save(Subscription subscription);

    int extendSubscription(
            @Param("memberId") Long memberId,
            @Param("targetId") Long targetId
    );

    int cancelSubscription(
            @Param("memberId") Long memberId,
            @Param("targetId") Long targetId
    );

    List<SubscriptionInfo> findMySubscriptions(
            @Param("memberId") Long memberId
    );

    int expireSubscriptions();
}