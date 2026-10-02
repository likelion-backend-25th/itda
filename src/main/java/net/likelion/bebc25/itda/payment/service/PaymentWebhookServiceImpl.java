package net.likelion.bebc25.itda.payment.service;


import net.likelion.bebc25.itda.payment.client.PortOneClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.likelion.bebc25.itda.exception.ErrorCode;
import net.likelion.bebc25.itda.payment.dto.Payment;
import net.likelion.bebc25.itda.payment.dto.PortOnePaymentResponse;
import net.likelion.bebc25.itda.payment.mapper.PaymentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentWebhookServiceImpl implements  PaymentWebhookService {

    private final PaymentMapper paymentMapper;
    private final PortOneClient portOneClient;


    /**
     * PortOne 결제 완료 Webhook 처리
     * <p>
     * 브라우저에서 /complete 호출이 누락된 경우
     * Webhook으로 전달받은 paymentId를 이용해
     * 실제 결제 상태를 확인하고 DB에 반영한다.
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void completePaymentByWebhook(String paymentId) {

        // 1. 우리 DB에서 결제 정보 조회
        Payment payment =
                paymentMapper.findByPaymentId(paymentId);


        log.info(
                "[WEBHOOK DB CHECK] paymentId={}, payment={}",
                paymentId,
                payment
        );

        if (payment == null) {
            throw new NoSuchElementException(
                    ErrorCode.RESOURCE_NOT_FOUND.getMessage()
                            + " 웹훅 결제 정보를 찾을 수 없습니다."
            );
        }

        Long paymentPendingStatusId =
                paymentMapper.findCommonCodeId(
                        1,
                        "PS01"
                );
        // 2. 결제 완료 상태 PS02 조회
        Long paidStatusId =
                paymentMapper.findCommonCodeId(
                        1,
                        "PS02"
                );

        if (paymentPendingStatusId == null) {
            throw new IllegalStateException(
                    ErrorCode.INTERNAL_SERVER_ERROR.getMessage()
                            + " 결제 대기 상태 코드가 없습니다."
            );
        }

        if (paidStatusId == null) {
            throw new IllegalStateException(
                    ErrorCode.INTERNAL_SERVER_ERROR.getMessage()
                            + " 결제 완료 상태 코드가 없습니다."
            );
        }

        // 3. 이미 결제 완료된 경우 중복 처리하지 않음
        if (paidStatusId.equals(payment.getStatusId())) {

            log.info(
                    "[WEBHOOK PAYMENT ALREADY COMPLETED] paymentId={}",
                    paymentId,
                    payment.getStatusId()
            );

            return;
        }

        // 4. PortOne 서버에서 실제 결제 정보 다시 조회
        PortOnePaymentResponse portOnePayment =
                portOneClient.getPayment(paymentId);

        if (portOnePayment == null) {
            throw new IllegalStateException(
                    ErrorCode.INTERNAL_SERVER_ERROR.getMessage()
                            + " PortOne 결제 정보를 조회할 수 없습니다."
            );
        }

        // 5. 실제 결제 상태가 PAID인지 확인
        if (!"PAID".equals(portOnePayment.status())) {
            throw new IllegalArgumentException(
                    ErrorCode.BUSINESS_RULE_VIOLATION.getMessage()
                            + " PortOne 결제 완료 상태가 아닙니다."
            );
        }

        // 6. PortOne 결제 금액 존재 여부 확인
        if (portOnePayment.amount() == null
                || portOnePayment.amount().total() == null) {

            throw new IllegalStateException(
                    ErrorCode.INTERNAL_SERVER_ERROR.getMessage()
                            + " PortOne 결제 금액이 없습니다."
            );
        }

        // 7. 우리 DB 금액과 실제 결제 금액 비교
        if (!payment.getAmount().equals(
                portOnePayment.amount().total()
        )) {
            throw new IllegalArgumentException(
                    ErrorCode.BUSINESS_RULE_VIOLATION.getMessage()
                            + " 결제 금액이 일치하지 않습니다."
            );
        }

        // 8. PortOne 거래 ID 확인
        if (portOnePayment.transactionId() == null) {
            throw new IllegalStateException(
                    ErrorCode.INTERNAL_SERVER_ERROR.getMessage()
                            + " PortOne transactionId가 없습니다."
            );
        }

        // 9. 실제 결제 완료 시간 확인
        if (portOnePayment.paidAt() == null) {
            throw new IllegalStateException(
                    ErrorCode.INTERNAL_SERVER_ERROR.getMessage()
                            + " PortOne 결제 완료 시간이 없습니다."
            );
        }

        // 10. payment 상태 PS01 → PS02 변경
        int updated = paymentMapper.updatePaid(
                        payment.getPaymentId(),
                        portOnePayment.transactionId(),
                        paidStatusId,
                        paymentPendingStatusId,
                        portOnePayment
                                .paidAt()
                                .toLocalDateTime()
                );

        // /complete 요청을 먼저 실행한 경우 먼저 PS01 -> PS02 처리되어 변화가 없다
        if (updated ==0) {
            // DB의 최신 결제 상태를 다시 조회
            Payment latestPayment = paymentMapper.findByPaymentId(paymentId);
            // 결제 데이터가 있는지 확인 체크 && 그 결제가 PS02인지
            if (latestPayment != null && paidStatusId.equals(latestPayment.getStatusId())) {
                log.info("[/complete에서 이미 결제를 완료했습니다.] paymentId={}, statusId={}",
                        paymentId, latestPayment.getStatusId()
                );
                return;
            }
            // PS02가 아닌 예상 못하는 상태 발생
            throw new IllegalStateException(
                    ErrorCode.INTERNAL_SERVER_ERROR.getMessage() + " Webhook 결제 상태 변경에 실패했습니다."
            );
        }

        // 결제 유형에 따라 후속 처리
        if (payment.getPaymentType().equals("THEME")) {

            // 기존 테마 구매 상태 조회
            Boolean purchaseStatus = paymentMapper.findThemePurchaseStatus(
                    payment.getMemberId(),
                    payment.getTargetId());

            // 구매 이력이 없는 경우
            if (purchaseStatus == null) {
                int insertedTheme = paymentMapper.insertThemePurchase(
                        payment.getMemberId(),
                        payment.getTargetId(),
                        payment.getId()
                );
                // 테마 구매 내역에 저장 되었는가 확인
                if (insertedTheme != 1) {
                    throw new IllegalStateException(
                            ErrorCode.INTERNAL_SERVER_ERROR.getMessage()
                                    + " 테마 구매 목록 저장 실패"
                    );
                }
                // 환불한 테마인 경우
            } else if (purchaseStatus == false) {

                int updatedTheme = paymentMapper.repurchaseTheme(
                        payment.getMemberId(),
                        payment.getTargetId(),
                        payment.getId()
                );
                if (updatedTheme != 1) {
                    throw new IllegalStateException(
                            ErrorCode.INTERNAL_SERVER_ERROR.getMessage() + " 테마 재구매 정보 갱신 실패"
                    );
                }
            }

        } else if (payment.getPaymentType().equals("SUBSCRIPTION")) {

            // 구독 활성 상태 코드 조회
            Long subscriptionStatusId =
                    paymentMapper.findCommonCodeId(
                            4,
                            "SS01" // 실제 구독 활성 코드로 맞추기
                    );
            // common_code의 구독 상태 코드가 없는 경우
            if (subscriptionStatusId == null) {
                throw new IllegalStateException(
                        ErrorCode.INTERNAL_SERVER_ERROR.getMessage() + " 구독 활성 코드가 null값입니다. "
                );
            }

            // 기존 구독 상태 조회
            Long currentSubscriptionStatus = paymentMapper.findSubscriptionStatus(
                    payment.getMemberId(),
                    payment.getTargetId()
            );

            // 최초 구독
            if (currentSubscriptionStatus == null) {
                int insertedSubscription = paymentMapper.insertSubscription(
                        payment.getMemberId(),
                        payment.getTargetId(),
                        subscriptionStatusId,
                        payment.getId()
                );
                if (insertedSubscription != 1) {
                    throw new IllegalStateException(
                            ErrorCode.INTERNAL_SERVER_ERROR.getMessage() + " 구독 저장이 실패했습니다."
                    );
                }
            } else {
                // 기존 구독 정보가 있는경우
                int updatedSubscription = paymentMapper.reactivateSubscription(
                        payment.getMemberId(),
                        payment.getTargetId(),
                        subscriptionStatusId,
                        payment.getId()
                );
                if (updatedSubscription != 1) {
                    throw new IllegalStateException(
                            ErrorCode.INTERNAL_SERVER_ERROR.getMessage() + " 재구독 상태 변경에 실패했습니다."
                    );
                }
            }
        }

        // Webhook 결제 완료 처리 성공 로그
        log.info(
                "[WEBHOOK PAYMENT COMPLETE] paymentId={}, transactionId={}, statusId={}",
                payment.getPaymentId(),
                portOnePayment.transactionId(),
                paidStatusId
        );
    }
}