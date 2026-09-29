package net.likelion.bebc25.itda.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PortOneCancelResponse(

        // PortOne에서 반환한 실제 취소 정보
        Cancellation cancellation
){
    // 결제 취소 상세 정보
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Cancellation(
            /**
             * 환불 성공 후
             * payment_refund.cancellation_id에 저장한다.
             */
            String id,

            // PortOne에서 제공하는 환불 성공 여부
            String status
    ){
    }
}
