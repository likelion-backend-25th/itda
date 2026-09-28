package net.likelion.bebc25.itda.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.OffsetDateTime;

// JSON을 Java 객체로 변환할 때, DTO에 없는 JSON 필드는 무시
@JsonIgnoreProperties(ignoreUnknown = true)
public record PortOnePaymentResponse(
        // PortOne의 결제 id == paymentId 체크
        String id,

        // 실제 결제 시도 식별자 payment dto의 transaction_id에 저장
        String transactionId,

        // READY / PAID / FAILED / CANCELLED ...
        String status,

        // Amount record의 실제 결제 금액 정보
        Amount amount,

        // 실제 결제 완료된 시간 한국기준?
        OffsetDateTime paidAt


) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Amount(
            // 실제 결제 총액
            Long total
    ) {
    }
}
