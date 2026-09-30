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
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final PortOneClient portOneClient;


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

        // PS01 = 결제 대기에서 시작
        Long statusId =
                paymentMapper.findCommonCodeId(
                        1,
                        "PS01"
                );

        // (프론트) 결제 수단이 요청값에 없거나 빈 문자열인 경우
        if (request.payMethod() == null || request.payMethod().isBlank()) {
            throw new IllegalArgumentException(
                    ErrorCode.INVALID_INPUT_VALUE.getMessage()
            );
        }


        //  프론트에서 선택한 결제 수단을 DB common_code와 매핑
        String payMethodCode = switch (request.payMethod()) {
            case "KAKAOPAY" -> "PM02";
            case "TOSSPAY" -> "PM03";
            // 지원하지 않는 결제 방식이 들어온 경우
            default -> throw new IllegalArgumentException(
                    ErrorCode.BUSINESS_RULE_VIOLATION.getMessage() + " 지원하지 않는 결제 방식입니다."
            );
        };

        String channelKey = switch ((request.payMethod())){
            case "KAKAOPAY" -> portOneClient.getKakaoPayChannelKey();
            case "TOSSPAY" -> portOneClient.getTossPayChannelKey();
            // 지원하지 않는 결제 방식이 들어온 경우
            default -> throw new IllegalArgumentException(
                    ErrorCode.BUSINESS_RULE_VIOLATION.getMessage() + " 지원하지 않는 결제 방식입니다."
            );
        };

        Long payMethodId =
                paymentMapper.findCommonCodeId(
                        2,
                        payMethodCode
                );

        // common_code null값으로 DB의 결제 상태 오류
        if (statusId == null) {
            throw new IllegalStateException(
                    ErrorCode.INTERNAL_SERVER_ERROR.getMessage()
            );
        }
        // 백엔드의 common_code null값으로 결제 수단이 비어 있음
        if (payMethodId == null) {
            throw new IllegalStateException(
                    ErrorCode.INTERNAL_SERVER_ERROR.getMessage()
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

        // 결제 대상 또는 가격 정보를 찾을 수 없는 경우
        if (amount == null) {
            throw new NoSuchElementException(
                    ErrorCode.RESOURCE_NOT_FOUND.getMessage()
            );
        }

        // PortOne은 totalAmount > 0 필수. 0원 테마는 /themes/{id}/claim 사용
        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "0원 상품은 결제가 불가능합니다. 무료 수령 API를 사용하세요."
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
         * 우리 DB에서 payment 조회
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
            throw new NoSuchElementException(
                    ErrorCode.RESOURCE_NOT_FOUND.getMessage()
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
         * 결제를 요청한 회원과
         * 현재 로그인한 회원이 동일한지 검증
         *
         * 다른 사용자의 paymentId를 이용한
         * 결제 완료 요청을 방지한다.
         */
        if (!payment.getMemberId().equals(memberId)) {
            throw new AuthorizationDeniedException(
                    ErrorCode.FORBIDDEN_OPERATION.getCode()
            );
        }


        /*
         * PS02 = 결제 완료
         *
         * AUTO_INCREMENT ID를 직접 2L로 사용하지 않고
         * common_code에서 실제 ID를 조회한다.
         */
        Long paidStatusId =
                paymentMapper.findCommonCodeId(
                        1,
                        "PS02"
                );

        // 결제 완료 상태가 코드가 없는경우 에러처리 데이터값 수정하지 않도록 주의
        if (paidStatusId == null) {
            throw new IllegalStateException(
                    ErrorCode.INTERNAL_SERVER_ERROR.getMessage()
            );
        }


        /*
         * 이미 결제 완료 처리된 결제인지 확인
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
         * PortOne 서버에서 실제 결제 정보 조회
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
         * paymentId 검증
         *
         * 우리 DB에 있는 paymentId와
         * PortOne이 반환한 결제 ID가 같은지 확인한다.
         */
        if (!payment.getPaymentId().equals(
                portOnePayment.id()
        )) {

            throw new IllegalArgumentException(
                    ErrorCode.BUSINESS_RULE_VIOLATION.getMessage() + " 결제 ID가 일치하지 않습니다."
            );
        }


        /*
         * 실제 결제 상태 검증
         *
         * PortOne 상태가 PAID일 때만
         * 정상 결제로 인정한다.
         * DB와 PortOne의 결제번호가 일치하는지 확인
         */
        if (!"PAID".equals(portOnePayment.status())) {
            throw new IllegalArgumentException(
                    ErrorCode.BUSINESS_RULE_VIOLATION.getMessage() + portOnePayment.status() + " 결제 완료 상태가 아닙니다."
            );
        }


        /*
         * PortOne 응답에 금액 정보가 존재하는지 확인
         */
        if (portOnePayment.amount() == null || portOnePayment.amount().total() == null) {
            throw new IllegalStateException(
                    ErrorCode.INTERNAL_SERVER_ERROR.getMessage() + " 금액이 비어있습니다."
            );
        }


        /*
         * 금액 위변조 검증
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
        if (!payment.getAmount().equals(portOnePayment.amount().total())) {
            throw new IllegalArgumentException(
                    ErrorCode.BUSINESS_RULE_VIOLATION.getMessage() + " 금액이 동일하지 않습니다."
            );
        }


        /*
         * 실제 PortOne 거래 ID 확인
         *
         * 결제가 완료되면 transactionId가 존재해야 한다.
         */
        if (portOnePayment.transactionId() == null) {
            throw new IllegalStateException(
                    ErrorCode.INTERNAL_SERVER_ERROR.getMessage() + " PortOne transactionId가 없습니다."
            );
        }


        /*
         * 실제 결제 완료 시간 확인
         */
        if (portOnePayment.paidAt() == null) {
            throw new IllegalStateException(
                    ErrorCode.INTERNAL_SERVER_ERROR.getMessage() + " 결제 시간이 없습니다."
            );
        }

        /*
         * 우리 DB 결제 완료 처리
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
            throw new RuntimeException(
                    ErrorCode.INTERNAL_SERVER_ERROR.getMessage() + " DB 변경 오류"
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
            // 결제는 완료되었지만 테마 구매 내역 저장에 실패한 경우
            if (insertedTheme != 1) {
                throw new IllegalStateException(
                        ErrorCode.INTERNAL_SERVER_ERROR.getMessage() + " 테마 구매 목록에 저장 실패"
                );
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

            // 구독 결제 완료 → subscription 저장
            int insertedSubscription =
                    paymentMapper.insertSubscription(
                            payment.getMemberId(),
                            payment.getTargetId(),
                            subscriptionStatusId,
                            payment.getId()
                    );
            // 결제는 완료되었지만 구독 내역 저장에 실패한 경우
            if (insertedSubscription != 1) {
                throw new IllegalStateException(
                        ErrorCode.INTERNAL_SERVER_ERROR.getMessage() + " 구독 저장이 실패하였습니다."
                );
            }
        }
        /*
         * 프론트에 결제 완료 결과 반환
         */
        return new PaymentCompleteResponse(

                payment.getPaymentId(),

                "PAID",

                payment.getAmount(),

                portOnePayment.transactionId()
        );
    }

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
     * @return 환불 결과 반환
     */
    @Override
    @Transactional
    public PaymentRefundResponse refundPayment(
            Long memberId,
            String paymentId,
            PaymentRefundRequest request
    ){
        // 환불 요청 시점
        LocalDateTime requestedAt = LocalDateTime.now();
        Payment payment = paymentMapper.findByPaymentId(paymentId);

        // 결제 정보를 찾을 수가 없는 에러처리
        if(payment == null){
            throw new NoSuchElementException(
                    ErrorCode.RESOURCE_NOT_FOUND.getMessage() + " 결제가 없습니다."
            );
        }
        // 본인의 결제가 맞는지 확인
        if(!payment.getMemberId().equals(memberId)){
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

        if("THEME".equals(payment.getPaymentType())) {
            // 테마인경우
            Boolean isUsed = paymentMapper.findThemeIsUsed(payment.getId());
            if(isUsed == null) {
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
        int updatedRefund = paymentMapper.updateRefunded(
                        payment.getPaymentId(),
                        paidStatusId,
                        refundedStatusId
                );

        if (updatedRefund != 1) {
            throw new IllegalStateException(
                    ErrorCode.INTERNAL_SERVER_ERROR.getMessage() + " 결제 환불 상태 변경에 실패했습니다."
            );
        }

        if("SUBSCRIPTION".equals(
                payment.getPaymentType()
        )){
            // 환불 후 구독 페이지에서 제거
            int deletedSubscription = paymentMapper.deleteByPaymentId(payment.getId());
            // 제거 여부 확인
            log.info("[SUBSCRIPTION DELETE] paymentId={}, deldteCount={}",
                    payment.getId(),
                    deletedSubscription
            );

        } else if ("THEME".equals(
                payment.getPaymentType()
        )) {
            // 환불 후 테마 결제 내역에서 삭제
            int deletedThemePurchase = paymentMapper.deleteThemePurchaseByPaymentId(payment.getId());
            // 제거 여부 확인
            log.info("[SUBSCRIPTION DELETE] paymentId={}, deldteCount={}",
                    payment.getId(),
                    deletedThemePurchase
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