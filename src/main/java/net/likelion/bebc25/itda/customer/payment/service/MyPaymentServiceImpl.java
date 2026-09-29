package net.likelion.bebc25.itda.customer.payment.service;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.customer.payment.dto.MyPaymentResponse;
import net.likelion.bebc25.itda.customer.payment.mapper.MyPaymentMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPaymentServiceImpl implements MyPaymentService {

    private final MyPaymentMapper myPaymentMapper;

    @Override
    public List<MyPaymentResponse> getMyPayments(Long memberId) {
        return myPaymentMapper.findMyPayments(memberId);
    }

    @Override
    public void requestRefund(Long memberId, Long paymentId) {
        int result = myPaymentMapper.requestRefund(memberId, paymentId);

        if (result == 0) {
            throw new IllegalArgumentException(
                    "환불 신청이 불가능한 결제입니다."
            );
        }
    }
}