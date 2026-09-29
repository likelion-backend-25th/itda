package net.likelion.bebc25.itda.payment.dto;

public record PaymentRefundResponse(
        // 기존 PortOne 결제 고유 ID
        String paymentId,
        // PortOne에서 발급한 취소 ID
        String cancellationId,
        // 환불 처리된 금액
        Long refundAmount,
        // 차감된 위약금
        Long deductionAmount
) {
}
