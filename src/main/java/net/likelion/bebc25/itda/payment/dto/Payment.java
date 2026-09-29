package net.likelion.bebc25.itda.payment.dto;

import java.time.LocalDateTime;



import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class Payment {
    // payment.id
    private Long id;

    // payment.member_id
    private Long memberId;

    // payment.payment_type
    private String paymentType;

    // payment.target_id
    private Long targetId;

    /**
     * 실제 결제가 완료된 후 PortOne 결제 조회 결과에서 받아서 저장
     * payment.transaction_id
     */
    private String transactionId;

    /**
     * 우리 서버에서 생성한 후 PortOne 결제 요청에 사용
     * payment.payment_id
     */
    private String paymentId;

    // payment.amount
    private Long amount;

    /**
     * 결제 상태
     * common_code.id 참조
     *
     * 1 = PS01 = 결제 대기
     * 2 = PS02 = 결제 완료
     * 3 = PS03 = 결제 취소
     * 4 = PS04 = 환불 완료
     *
     * payment.status_id
     */
    private Long statusId;

    /**
     * 결제 수단
     * common_code.id 참조
     *
     * 5 = PM01 = 카드
     * 6 = PM02 = 카카오페이
     * 7 = PM03 = 토스페이
     *
     * payment.pay_method_id
     */
    private Long payMethodId;

    /**
     * 실제 결제 완료 시간
     *
     * 결제 준비 단계에서는 null
     *
     * payment.paid_at
     */
    private LocalDateTime paidAt;

    // getter가 없는경우 @NoArgsConstructor에 의해 Lombok에서 자동생성
    public Payment(Long memberId, String paymentType, Long targetId, String paymentId, Long amount, Long statusId, Long payMethodId) {
        this.memberId = memberId;
        this.paymentType = paymentType;
        this.targetId = targetId;
        this.paymentId = paymentId;
        this.amount = amount;
        this.statusId = statusId;
        this.payMethodId = payMethodId;
    }
}
