package net.likelion.bebc25.itda.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentWebhookRequest(

        // Transaction.Paid 등 웹훅 이벤트 종류
        String type,

        // PortOne에서 이벤트가 발생한 시간
        // Z(UTC)가 포함되므로 OffsetDateTime 사용
        OffsetDateTime timestamp,

        // 실제 결제 관련 데이터
        WebhookData data

) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record WebhookData(

            // PortOne 거래 ID
            String transactionId,

            // 우리가 생성한 결제 ID
            // 실제 결제에서는 ITDA-xxxx 형식
            String paymentId,

            // PortOne 상점 ID
            String storeId
    ) {
    }
}