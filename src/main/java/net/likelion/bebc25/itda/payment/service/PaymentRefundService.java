package net.likelion.bebc25.itda.payment.service;

import net.likelion.bebc25.itda.payment.dto.PaymentRefundRequest;
import net.likelion.bebc25.itda.payment.dto.PaymentRefundResponse;

public interface PaymentRefundService {

    /**
     * 결제 환불 처리
     *
     * 결제 정보와 사용자 권한을 검증하고,
     * 결제 유형에 따른 환불 금액을 계산한 뒤
     * PortOne 환불 및 DB 상태 변경을 처리한다.
     */
    PaymentRefundResponse refundPayment(
            Long memberId,
            String paymentId,
            PaymentRefundRequest request
    );
}