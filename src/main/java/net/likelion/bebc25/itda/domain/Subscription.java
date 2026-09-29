package net.likelion.bebc25.itda.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Subscription {

    private Long id;
    private Long member_id;       // 구독자
    private Long target_id;       // 구독 대상 크리에이터
    private String billing_key;  // 정기결제용 빌링키
    private Long price_id;        // 구독 가격 CommonCode ID
    private Long status_id;       // 구독 상태 CommonCode ID
    private LocalDateTime next_billing_at;
    private LocalDateTime started_at;
    private LocalDateTime ended_at;
    private Long payment_id;      // 연결된 결제 ID
}