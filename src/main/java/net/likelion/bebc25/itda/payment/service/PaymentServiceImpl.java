package net.likelion.bebc25.itda.payment.service;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.payment.client.PortOneClient;
import net.likelion.bebc25.itda.payment.dto.Payment;
import net.likelion.bebc25.itda.payment.dto.PaymentPrepareRequest;
import net.likelion.bebc25.itda.payment.dto.PaymentPrepareResponse;
import net.likelion.bebc25.itda.payment.mapper.PaymentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final PortOneClient portOneClient;

    // 결제 기능 테스트용 임시 금액
    private static final Long TEST_AMOUNT = 100L;

    /**
     * 결제 준비
     *
     * 1. 결제 상태/수단 조회
     * 2. paymentId 생성
     * 3. payment 테이블에 결제 대기 상태 저장
     * 4. PortOne에 결제 정보 사전 등록
     * 5. 프론트에 paymentId, amount 반환
     */
    @Override
    @Transactional
    public PaymentPrepareResponse preparePayment(
            Long memberId,
            PaymentPrepareRequest request
    ) {

        // PS01 = 결제 대기
        Long statusId =
                paymentMapper.findCommonCodeId(
                        1,
                        "PS01"
                );

        // PM02 = 카카오페이
        Long payMethodId =
                paymentMapper.findCommonCodeId(
                        2,
                        "PM02"
                );

        if (statusId == null) {
            throw new IllegalStateException(
                    "결제 대기 상태 코드가 없습니다."
            );
        }

        if (payMethodId == null) {
            throw new IllegalStateException(
                    "카카오페이 결제 수단 코드가 없습니다."
            );
        }

        // PortOne V2에서 사용할 결제 고유 ID 생성
        String paymentId =
                "ITDA-" + UUID.randomUUID()
                        .toString()
                        .replace("-", "");

        // 결제 대기 정보 생성
        Payment payment =
                new Payment(
                        memberId,
                        request.paymentType(),
                        request.targetId(),
                        paymentId,
                        TEST_AMOUNT,
                        statusId,
                        payMethodId
                );

        // payment 테이블 저장
        paymentMapper.insert(payment);

        // PortOne 서버에 결제 금액 사전 등록
        portOneClient.preRegister(
                paymentId,
                TEST_AMOUNT
        );

        // 프론트에 결제 준비 결과 반환
        return new PaymentPrepareResponse(
                paymentId,
                TEST_AMOUNT
        );
    }
}