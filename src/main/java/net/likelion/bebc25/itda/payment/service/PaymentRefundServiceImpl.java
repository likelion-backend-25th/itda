package net.likelion.bebc25.itda.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.likelion.bebc25.itda.exception.ErrorCode;
import net.likelion.bebc25.itda.payment.client.PortOneClient;
import net.likelion.bebc25.itda.payment.dto.*;
import net.likelion.bebc25.itda.payment.mapper.PaymentMapper;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentRefundServiceImpl implements PaymentRefundService {

    private final PaymentMapper paymentMapper;
    private final PortOneClient portOneClient;

    /**
     * payment 조회
     * 본인 결제인지 확인
     * 결제 완료 상태인지(PS02) 인지 확인
     * 결제 유형에 따른 환불 금액 계산
     * PortOne 취소 api 호출
     * PortOne 취소 성공 여부 확인
     * payment_refound 저장
     * payment 상태 PS02 결제 완료 -> PS04 환불 완료
     * 구독인 경우 SUBSCRIPTION 상태 SS02로 변경 구독 취소
     *
     * @return 환불 결과 반환
     */
    @Override
    @Transactional
    public PaymentRefundResponse refundPayment(
            Long memberId,
            String paymentId,
            PaymentRefundRequest request
    ) {
        // 환불 요청 시점
        LocalDateTime requestedAt = LocalDateTime.now();
        Payment payment = paymentMapper.findByPaymentId(paymentId);

        // 결제 정보를 찾을 수가 없는 에러처리
        if (payment == null) {
            throw new NoSuchElementException(
                    ErrorCode.RESOURCE_NOT_FOUND.getMessage() + " 결제가 없습니다."
            );
        }
        // 본인의 결제가 맞는지 확인
        if (!payment.getMemberId().equals(memberId)) {
            throw new AuthorizationDeniedException(
                    ErrorCode.FORBIDDEN_OPERATION.getCode() + "회원님의 결제가 아닙니다."
            );
        }
        Long paidStatusId = paymentMapper.findCommonCodeId(
                1,
                "PS02"
        );

        /**
         * 결제 완료 상태가 아닌경우 환불
         * PS01, PS03, PS04인 상태에서 환불은 불가능
         */
        if (!paidStatusId.equals(payment.getStatusId())) {
            throw new IllegalArgumentException(
                    ErrorCode.BUSINESS_RULE_VIOLATION.getMessage() + " 환불 가능한 결제 상태가 아닙니다."
            );
        }


        long refundAmount; // 최종 실제 환불 금액
        long deductionAmount; // 차감된 금액

        if ("THEME".equals(payment.getPaymentType())) {
            // 테마인경우
            Boolean isUsed = paymentMapper.findThemeIsUsed(payment.getId());
            if (isUsed == null) {
                throw new IllegalStateException(
                        ErrorCode.INTERNAL_SERVER_ERROR.getMessage() + " 테마 사용 여부를 알 수 없습니다."
                );
            } else if (isUsed) {
                throw new IllegalArgumentException(
                        ErrorCode.BUSINESS_RULE_VIOLATION.getMessage() + "이미 사용하셨습니다."
                );
            } else {
                // 미사용 테마는 전액 환불
                refundAmount = payment.getAmount();
                deductionAmount = 0L;
            }


        } else if ("SUBSCRIPTION".equals(payment.getPaymentType())) {
            // 구독인경우
            // 구독 시작 날짜 조회
            LocalDateTime startedAt = paymentMapper.findSubscriptionStartedAt(payment.getId());

            if (startedAt == null) {
                throw new NoSuchElementException(
                        ErrorCode.RESOURCE_NOT_FOUND.getMessage() + " 구독을 찾을 수 없습니다."
                );
            }
            // 구독 기간은 30일을 기준으로 차감
            long totalDays = 30L;
            // 사용한 날짜
            long usedDays = ChronoUnit.DAYS.between(
                    startedAt.toLocalDate(),
                    LocalDate.now()
            );
            long remainingDays = totalDays - usedDays; // 남은 일수

            if (remainingDays <= 0 || remainingDays > 30) {
                throw new IllegalArgumentException(
                        ErrorCode.BUSINESS_RULE_VIOLATION.getMessage() + " 구독 잔여 기간 정보가 올바르지 않습니다."
                );
            }
            // 남는 가격은 금액에 날짜 만큼 차감
            long remainingAmount = payment.getAmount() * remainingDays / totalDays;
            // 위약금은 남는 금액의 10%
            deductionAmount = remainingAmount * 10 / 100;
            // 총 환불 금액은 남는 가격 - 위약금
            refundAmount = remainingAmount - deductionAmount;

        } else {
            throw new IllegalArgumentException(
                    ErrorCode.BUSINESS_RULE_VIOLATION.getMessage() + " 지원하지 않는 결제 유형입니다."
            );
        }

        // PortOne 서버에 실제 환불 요청
        PortOneCancelResponse cancelResponse = portOneClient.cancelResponse(
                payment.getPaymentId(),
                request.reason(),
                refundAmount
        );
        //  PortOne 응답 여부 확인
        if (cancelResponse == null || cancelResponse.cancellation() == null) {
            throw new IllegalStateException(
                    ErrorCode.INTERNAL_SERVER_ERROR.getMessage() + " PortOne 환불 응답이 없습니다."
            );
        }
        // 실제 환불 성공 여부 확인
        if (!"SUCCEEDED".equals(cancelResponse.cancellation().status() // PortOne에서 보내주는 고정값
        )) {
            throw new IllegalStateException(
                    ErrorCode.INTERNAL_SERVER_ERROR.getMessage() + " PortOne 환불이 완료되지 않았습니다."
            );
        }

        // 환불 고유 ID 생성 여부
        String cancellationId = cancelResponse.cancellation().id();
        if (cancellationId == null || cancellationId.isBlank()) {
            throw new IllegalStateException(
                    ErrorCode.INTERNAL_SERVER_ERROR.getMessage() + " PortOne이 전달한 cancellationId가 없습니다."
            );
        }

        // 환불 완료 시간
        LocalDateTime refundedAt = LocalDateTime.now();
        // 환불 내역 저장
        int insertedRefund = paymentMapper.insertPaymentRefund(
                payment.getId(),
                cancellationId,
                refundAmount,
                deductionAmount,
                request.reason(),
                requestedAt,
                refundedAt
        );

        // 환불 내역 저장 테이블 확인
        if (insertedRefund != 1) {
            throw new IllegalStateException(
                    ErrorCode.INTERNAL_SERVER_ERROR.getMessage() + " 환불 내역 저장에 실패했습니다."
            );
        }

        // code PS02 결제 완료 -> PS04 환불 완료
        Long refundedStatusId = paymentMapper.findCommonCodeId(
                1,
                "PS04"
        );

        // 환불 후 결제 상태 갱신
        int updatedRefund = paymentMapper.updatePaymentRefundStatus(
                payment.getPaymentId(),
                paidStatusId,
                refundedStatusId
        );

        if (updatedRefund != 1) {
            throw new IllegalStateException(
                    ErrorCode.INTERNAL_SERVER_ERROR.getMessage() + " 결제 환불 상태 변경에 실패했습니다."
            );
        }

        if ("SUBSCRIPTION".equals(
                payment.getPaymentType()
        )) {
            // 구독 취소 상태 코드 조회 SS02
            Long cancelledStatusId = paymentMapper.findCommonCodeId(4, "SS02");

            // 구독 취소 코드가 없는 경우
            if (cancelledStatusId == null) {
                throw new IllegalStateException(
                        ErrorCode.INTERNAL_SERVER_ERROR.getMessage() + " 구독 취소 상태 코드가 없습니다."
                );
            }
            // 구독 이력은 삭제하지 않고 취소 상태로 변경
            int updatedSubscription = paymentMapper.updateSubscriptionCancelStatus(
                    payment.getId(),
                    cancelledStatusId
            );

            if (updatedSubscription != 1) {
                throw new IllegalStateException(
                        ErrorCode.INTERNAL_SERVER_ERROR.getMessage() + " 구독 상태 변경에 실패했습니다."
                );
            }
            // 구독 취소/ 결제ID와 구독 취소 상태를 보여줌
            log.info(
                    "[SUBSCRIPTION CANCELLED] paymentId={}, statusId={}",
                    payment.getId(),
                    cancelledStatusId
            );


        } else if ("THEME".equals(
                payment.getPaymentType()
        )) {
            // 환불 대상 테마를 현재 사용 중이면 기본 테마로 변경
            paymentMapper.resetThemeToDefaultByPaymentId(payment.getId());

            // 환불 후 테마 결제 내역의 상태 초기화
            int updatedThemePurchase = paymentMapper.updateRefundStatusByPaymentId(payment.getId());
            // 제거 여부 확인
            log.info("[THEME PURCHASE REFUNDED] paymentId={}, deldteCount={}",
                    payment.getId(),
                    updatedThemePurchase
            );
        }
        log.info("[PAYMENT REFUND SUCCESS] paymentId={}, cancellationId={}, refoundAmount={}, deductionAmount={}",
                payment.getPaymentId(),
                cancellationId,
                refundAmount,
                deductionAmount
        );

        return new PaymentRefundResponse(
                payment.getPaymentId(),
                cancellationId,
                refundAmount,
                deductionAmount
        );
    }
}