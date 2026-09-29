package net.likelion.bebc25.itda.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.likelion.bebc25.itda.payment.client.PortOneClient;
import net.likelion.bebc25.itda.payment.dto.*;
import net.likelion.bebc25.itda.payment.mapper.PaymentMapper;
import net.likelion.bebc25.itda.subscription.service.SubscriptionServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final PortOneClient portOneClient;
    private final SubscriptionServiceImpl subscriptionServiceImpl;


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
        // 1. 결제 준비 요청 시작
        log.info(
                "[01 PAYMENT PREPARE START] memberId={}, paymentType={}, targetId={}",
                memberId,
                request.paymentType(),
                request.targetId()
        );

        // PS01 = 결제 대기
        Long statusId =
                paymentMapper.findCommonCodeId(
                        1,
                        "PS01"
                );

        if (request.payMethod() == null || request.payMethod().isBlank()) {
            throw new IllegalArgumentException("payMethod가 전달되지 않았습니다.");
        }
        //  프론트에서 선택한 결제 수단을 DB common_code와 매핑
        String payMethodCode = switch (request.payMethod()) {
            case "KAKAOPAY" -> "PM02";
            case "TOSSPAY" -> "PM03";
            default -> throw new IllegalArgumentException(
                    "지원하지 않는 결제 수단입니다."
            );
        };

        String channelKey = switch ((request.payMethod())){
            case "KAKAOPAY" -> portOneClient.getKakaoPayChannelKey();
            case "TOSSPAY" -> portOneClient.getTossPayChannelKey();
            default -> throw new IllegalArgumentException(
                    "지원하지 않는 결제 수단입니다.");
        };

        Long payMethodId =
                paymentMapper.findCommonCodeId(
                        2,
                        payMethodCode
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

        // 결제 유형과 대상 ID를 기준으로 실제 결제 금액 조회
        Long amount =
                paymentMapper.findAmount(
                        request.paymentType(),
                        request.targetId()
                );

        if (amount == null) {
            throw new IllegalArgumentException(
                    "결제 대상의 가격 정보를 찾을 수 없습니다."
            );
        }


        // 결제 대기 정보 생성
        Payment payment =
                new Payment(
                        memberId,
                        request.paymentType(),
                        request.targetId(),
                        paymentId,
                        amount,
                        statusId,
                        payMethodId
                );

        // 02. DB 저장 직전 결제 객체 확인
        log.info(
                "[02 PAYMENT CREATED] paymentId={}, memberId={}, amount={}, statusId={}, payMethodId={}",
                payment.getPaymentId(),
                payment.getMemberId(),
                payment.getAmount(),
                payment.getStatusId(),
                payment.getPayMethodId()
        );


        // payment 테이블 저장
        paymentMapper.insertPayment(payment);


        // PortOne 서버에 결제 금액 사전 등록
        portOneClient.preRegister(
                paymentId,
                amount
        );

        // 03. 준비 단계 전체 성공 로그
        log.info(
                "[03 PAYMENT PREPARE SUCCESS] paymentId={}, memberId={}, paymentType={}, targetId={}, amount={}",
                paymentId,
                memberId,
                request.paymentType(),
                request.targetId(),
                amount
        );

        // 프론트에 결제 준비 결과 반환
        return new PaymentPrepareResponse(
                paymentId,
                amount,
                portOneClient.getStoreId(),
                channelKey
        );
    }
    /**
     * 결제 완료 검증
     *
     * 처리 순서
     *
     * 1. 우리 DB에서 payment 조회
     * 2. 해당 결제가 현재 로그인 사용자의 결제인지 확인
     * 3. 결제 완료 코드 PS02 조회
     * 4. 중복 완료 처리 방지
     * 5. PortOne 서버에서 실제 결제 정보 조회
     * 6. paymentId 검증
     * 7. 결제 상태 PAID 검증
     * 8. 결제 금액 검증
     * 9. transactionId 확인
     * 10. 결제 완료 시간 확인
     * 11. payment 테이블 PS01 → PS02 변경
     * 12. 결과 반환
     */
    @Override
    @Transactional
    public PaymentCompleteResponse completePayment(
            Long memberId,
            PaymentCompleteRequest request
    ) {

        /*
         * 1. 우리 DB에서 payment 조회
         *
         * prepare 단계에서 저장한 결제 정보를 가져온다.
         */
        Payment payment =
                paymentMapper.findByPaymentId(
                        request.paymentId()
                );


        /*
         * paymentId가 DB에 존재하지 않는 경우
         */
        if (payment == null) {
            throw new IllegalArgumentException(
                    "존재하지 않는 결제입니다."
            );
        }
        //  04.  DB에서 조회한 결제 대기 정보 확인
        log.info(
                "[04 PAYMENT DB FOUND] paymentId={}, memberId={}, amount={}, statusId={}, transactionId={}",
                payment.getPaymentId(),
                payment.getMemberId(),
                payment.getAmount(),
                payment.getStatusId(),
                payment.getTransactionId()
        );


        /*
         * 2. 결제를 요청한 회원과
         * 현재 로그인한 회원이 동일한지 검증
         *
         * 다른 사용자의 paymentId를 이용한
         * 결제 완료 요청을 방지한다.
         */
        if (!payment.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException(
                    "본인의 결제가 아닙니다."
            );
        }


        /*
         * 3. PS02 = 결제 완료
         *
         * AUTO_INCREMENT ID를 직접 2L로 사용하지 않고
         * common_code에서 실제 ID를 조회한다.
         */
        Long paidStatusId =
                paymentMapper.findCommonCodeId(
                        1,
                        "PS02"
                );


        if (paidStatusId == null) {
            throw new IllegalStateException(
                    "결제 완료 상태 코드가 없습니다."
            );
        }


        /*
         * 4. 이미 결제 완료 처리된 결제인지 확인
         *
         * 사용자가 새로고침하거나
         * complete API를 두 번 호출하더라도
         * 중복 UPDATE하지 않는다.
         */
        if (paidStatusId.equals(
                payment.getStatusId()
        )) {

            return new PaymentCompleteResponse(
                    payment.getPaymentId(),
                    "PAID",
                    payment.getAmount(),
                    payment.getTransactionId()
            );
        }


        /*
         * 5. ⭐ PortOne 서버에서 실제 결제 정보 조회
         *
         * 프론트에서 "결제 성공"이라고 전달한 값을
         * 그대로 신뢰하지 않는다.
         */
        PortOnePaymentResponse portOnePayment =
                portOneClient.getPayment(
                        request.paymentId()
                );

        if (portOnePayment == null) {
            throw new IllegalStateException(
                    "PortOne 결제 정보를 조회할 수 없습니다."
            );
        }
        // 05. PortOne 응답 테스트
        log.info(
                "[05 PORTONE PAYMENT FOUND] paymentId={}, status={}, transactionId={}, paidAt={}",
                portOnePayment.id(),
                portOnePayment.status(),
                portOnePayment.transactionId(),
                portOnePayment.paidAt()
        );


        /*
         * 6. ⭐ paymentId 검증
         *
         * 우리 DB에 있는 paymentId와
         * PortOne이 반환한 결제 ID가 같은지 확인한다.
         */
        if (!payment.getPaymentId().equals(
                portOnePayment.id()
        )) {

            throw new IllegalStateException(
                    "결제 ID가 일치하지 않습니다."
            );
        }


        /*
         * 7. ⭐ 실제 결제 상태 검증
         *
         * PortOne 상태가 PAID일 때만
         * 정상 결제로 인정한다.
         */
        if (!"PAID".equals(
                portOnePayment.status()
        )) {

            throw new IllegalStateException(
                    "결제가 완료되지 않았습니다. status="
                            + portOnePayment.status()
            );
        }


        /*
         * 8-1. PortOne 응답에 금액 정보가 존재하는지 확인
         */
        if (portOnePayment.amount() == null
                || portOnePayment.amount().total() == null) {

            throw new IllegalStateException(
                    "PortOne 결제 금액 정보가 없습니다."
            );
        }


        /*
         * 8-2. ⭐ 금액 위변조 검증
         *
         * prepare 단계에서 DB에 저장한 금액과
         * PortOne에서 실제 결제된 금액을 비교한다.
         *
         * 현재 테스트:
         *
         * DB = 100원
         * PortOne = 100원
         *
         * 둘이 동일해야 정상 결제로 처리한다.
         */
        if (!payment.getAmount().equals(
                portOnePayment.amount().total()
        )) {

            throw new IllegalStateException(
                    "결제 금액이 일치하지 않습니다."
            );
        }


        /*
         * 9. 실제 PortOne 거래 ID 확인
         *
         * 결제가 완료되면 transactionId가 존재해야 한다.
         */
        if (portOnePayment.transactionId() == null) {
            throw new IllegalStateException(
                    "PortOne transactionId가 없습니다."
            );
        }


        /*
         * 10. 실제 결제 완료 시간 확인
         */
        if (portOnePayment.paidAt() == null) {
            throw new IllegalStateException(
                    "PortOne 결제 완료 시간이 없습니다."
            );
        }

        /*
         * 11. ⭐ 우리 DB 결제 완료 처리
         *
         * 기존
         *
         * status_id     = PS01
         * transactionId = NULL
         * paid_at       = NULL
         *
         * 변경 후
         *
         * status_id     = PS02
         * transactionId = PortOne 실제 거래 ID
         * paid_at       = 실제 결제 완료 시간
         */
        int updated =
                paymentMapper.updatePaid(

                        payment.getPaymentId(),

                        portOnePayment.transactionId(),

                        paidStatusId,

                        portOnePayment
                                .paidAt()
                                .toLocalDateTime()
                );


        /*
         * UPDATE 결과가 1건이 아니면
         * 정상적으로 DB가 변경되지 않은 상태
         */
        if (updated != 1) {
            throw new IllegalStateException(
                    "결제 완료 상태 저장에 실패했습니다."
            );
        }

        // 06. DB 결제 완료 상태 저장 성공 테스트
        log.info(
                "[06 PAYMENT COMPLETE SUCCESS] paymentId={}, transactionId={}, statusId={}, paidAt={}",
                payment.getPaymentId(),
                portOnePayment.transactionId(),
                paidStatusId,
                portOnePayment.paidAt()
        );

        // 결제 유형에 따라 후속 처리
        if (payment.getPaymentType().equals("THEME")) {

            // 테마 결제 완료 → theme_purchase 저장
            int insertedTheme =
                    paymentMapper.insertThemePurchase(
                            payment.getMemberId(),
                            payment.getTargetId(),
                            payment.getId()
                    );

            if (insertedTheme != 1) {
                throw new IllegalStateException(
                        "테마 구매 내역 저장에 실패했습니다."
                );
            }

        } else if (payment.getPaymentType().equals("SUBSCRIPTION")) {

            // 구독 활성 상태 코드 조회
            Long subscriptionStatusId =
                    paymentMapper.findCommonCodeId(
                            4,
                            "SS01" // 실제 구독 활성 코드로 맞추기
                    );

            if (subscriptionStatusId == null) {
                throw new IllegalStateException(
                        "구독 활성 상태 코드가 없습니다."
                );
            }

            // 구독 결제 완료 → subscription 저장
            int insertedSubscription =
                    paymentMapper.insertSubscription(
                            payment.getMemberId(),
                            payment.getTargetId(),
                            subscriptionStatusId,
                            payment.getId()
                    );

            if (insertedSubscription != 1) {
                throw new IllegalStateException(
                        "구독 내역 저장에 실패했습니다."
                );
            }
        }
        /*
         * 12. 프론트에 결제 완료 결과 반환
         */
        return new PaymentCompleteResponse(

                payment.getPaymentId(),

                "PAID",

                payment.getAmount(),

                portOnePayment.transactionId()
        );
    }
}