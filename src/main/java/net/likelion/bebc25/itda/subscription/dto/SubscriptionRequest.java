package net.likelion.bebc25.itda.subscription.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SubscriptionRequest {

    @NotNull(message = "구독 대상 회원 ID는 필수입니다.")
    private Long targetId;

    @NotNull(message = "구독 가격 ID는 필수입니다.")
    private Long priceId;
}