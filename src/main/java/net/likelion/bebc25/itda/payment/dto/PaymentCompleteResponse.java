package net.likelion.bebc25.itda.payment.dto;

public record PaymentCompleteResponse(

        String paymentId,
        String status,
        Long amount,
        String transactionId

) {
}