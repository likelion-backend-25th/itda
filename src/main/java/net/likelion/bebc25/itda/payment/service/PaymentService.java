package net.likelion.bebc25.itda.payment.service;

import net.likelion.bebc25.itda.payment.dto.PaymentCompleteRequest;
import net.likelion.bebc25.itda.payment.dto.PaymentCompleteResponse;
import net.likelion.bebc25.itda.payment.dto.PaymentPrepareRequest;
import net.likelion.bebc25.itda.payment.dto.PaymentPrepareResponse;

public interface PaymentService {
    // 결제 준비
    PaymentPrepareResponse preparePayment(
            Long memberId,
            PaymentPrepareRequest request
    );

    /**
     * 결제 검증 완료
     * PortOne에서 실제 결제 정보를 조회화고
     * 금액과 결제 상태를 검증한 후
     * 우리 DB를 결제 완료 상태로 변경
     */
    PaymentCompleteResponse completePayment(
            Long memberId,
            PaymentCompleteRequest request
    );
}
