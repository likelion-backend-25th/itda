package net.likelion.bebc25.itda.payment.dto;

public record PortOneCancelRequest(
        // 전달할 환불 사유
        String reason,
        /**
         * 테마 사용시 환불 불가
         * 구독은 남은 일 수에서 차감
         */
        Long amount
) {
}
