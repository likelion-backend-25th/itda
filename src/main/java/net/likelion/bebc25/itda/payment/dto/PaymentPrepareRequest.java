package net.likelion.bebc25.itda.payment.dto;



import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import static aQute.bnd.annotation.headers.Category.example;

/**
 * 결제 준비 요청 DTO
 */
@Schema(description = "결제 요청 준비")
public record PaymentPrepareRequest(
        /**
         * 결제 유형
         * 테마 / 구독
         */
        @Schema(description = "상품 유형",
                example = "THENE"
        )
        @NotBlank(message = "결제 유형은 필수입니다.")
        String paymentType,

        /**
         *
         */
        @Schema(description = "결제 대상 ID",
                example = "1"
        )
        @NotNull(message = "결제 대상 ID는 필수입니다.")
        Long targetId
) {
}