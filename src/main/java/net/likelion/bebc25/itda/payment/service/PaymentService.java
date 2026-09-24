package net.likelion.bebc25.itda.payment.service;

import net.likelion.bebc25.itda.payment.dto.PaymentPrepareRequest;
import net.likelion.bebc25.itda.payment.dto.PaymentPrepareResponse;

public interface PaymentService {
    PaymentPrepareResponse preparePayment(
            Long memberId,
            PaymentPrepareRequest request
    );
}
