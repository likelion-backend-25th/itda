package net.likelion.bebc25.itda.admin.payment.service;

import net.likelion.bebc25.itda.admin.payment.dto.AdminPaymentResponse;
import net.likelion.bebc25.itda.admin.payment.dto.AdminRefundResponse;

import java.util.List;

public interface AdminPaymentService {

    List<AdminPaymentResponse> getAllPayments();

    List<AdminRefundResponse> getAllRefunds();

    void refundPayment(Long paymentId);

}