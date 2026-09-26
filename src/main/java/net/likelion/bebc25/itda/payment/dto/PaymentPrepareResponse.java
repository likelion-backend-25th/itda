package net.likelion.bebc25.itda.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PaymentPrepareResponse(

        /**
         * PortOne V2에서 사용할 결제 고유 ID
         *
         * 백엔드에서 생성하고,
         * 프론트의 PortOne.requestPayment() 호출 시 사용
         */
        @Schema(
                description = "PortOne V2 결제 고유 ID",
                example = "ITDA-550e8400e29b41d4a716446655440000"
        )
        String paymentId,



        /**
         * 실제 결제 금액
         *
         * 프론트에서 전달받는 값이 아니라
         * 백엔드에서 결정한 금액
         */
        @Schema(
                description = "결제 금액",
                example = "3000"
        )
        Long amount
) {}