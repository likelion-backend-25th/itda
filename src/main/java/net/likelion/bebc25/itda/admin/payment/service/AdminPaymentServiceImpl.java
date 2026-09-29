package net.likelion.bebc25.itda.admin.payment.service;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.admin.payment.dto.AdminPaymentResponse;
import net.likelion.bebc25.itda.admin.payment.dto.AdminRefundResponse;
import net.likelion.bebc25.itda.admin.payment.mapper.AdminPaymentMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
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
    public void refundPayment(Long paymentId) {

        int updated = adminPaymentMapper.refundPayment(paymentId);

        if (updated == 0) {
            throw new IllegalArgumentException(
                    "환불할 수 없는 결제입니다."
            );
        }
    }
}