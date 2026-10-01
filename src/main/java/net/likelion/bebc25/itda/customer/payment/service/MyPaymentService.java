package net.likelion.bebc25.itda.customer.payment.service;

import net.likelion.bebc25.itda.customer.payment.dto.MyPaymentResponse;

import java.util.List;

public interface MyPaymentService {
    List<MyPaymentResponse> getMyPayments(Long memberId);
}
