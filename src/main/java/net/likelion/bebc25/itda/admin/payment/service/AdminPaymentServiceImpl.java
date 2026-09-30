package net.likelion.bebc25.itda.admin.payment.service;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.admin.payment.dto.AdminPaymentResponse;
import net.likelion.bebc25.itda.admin.payment.dto.AdminRefundResponse;
import net.likelion.bebc25.itda.admin.payment.mapper.AdminPaymentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminPaymentServiceImpl implements AdminPaymentService {

    private final AdminPaymentMapper adminPaymentMapper;

    @Override
    public List<AdminPaymentResponse> getAllPayments() {
        return adminPaymentMapper.findAllPayments();
    }

    @Override
    public List<AdminRefundResponse> getAllRefunds() {
        return adminPaymentMapper.findAllRefunds();
    }

    @Override
    @Transactional
    public void refundPayment(Long paymentId) {

        int updated = adminPaymentMapper.refundPayment(paymentId);

        if (updated == 0) {
            throw new IllegalArgumentException(
                    "환불할 수 없는 결제입니다."
            );
        }
    }

    @Override
    @Transactional
    public void rejectRefund(Long paymentId) {

        int updated = adminPaymentMapper.rejectRefund(paymentId);

        if (updated == 0) {
            throw new IllegalArgumentException(
                    "환불 거부할 수 없는 결제입니다."
            );
        }
    }
}