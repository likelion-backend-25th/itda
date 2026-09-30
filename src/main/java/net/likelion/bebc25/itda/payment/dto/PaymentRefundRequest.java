package net.likelion.bebc25.itda.payment.dto;
import jakarta.validation.constraints.NotBlank;



// 환불 처리 응답 DTO 생성
public record PaymentRefundRequest(
        // 환불 사유
        @NotBlank(message = "환불 사유는 필수입니다.")
        String reason
) {

        // default null 이므로 환불에 필요한 사유는 자동으로 처리
        public PaymentRefundRequest {
                if(reason == null || reason.isBlank()){
                        reason = "사용자 요청";
                }
        }
}
