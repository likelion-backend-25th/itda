package net.likelion.bebc25.itda.payment.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 결제 완료 검증 요청 DTO
 * 결제 후 결제 완료 요청 (paymentId를 백엔드로 전달, 결제 아이디를 가지고 payment DB 조회)
 * PortOne 서버에 실제 결제 정보를 조회한다.
 *
 */
public record PaymentCompleteRequest(

        @NotBlank
        String paymentId

) {
}