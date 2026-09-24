package net.likelion.bebc25.itda.subscription.mapper;

import net.likelion.bebc25.itda.domain.Subscription;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SubscriptionMapper {

    Subscription findActiveSubscription(
            @Param("memberId") Long memberId,
            @Param("targetId") Long targetId
    );

    int save(Subscription subscription);
}