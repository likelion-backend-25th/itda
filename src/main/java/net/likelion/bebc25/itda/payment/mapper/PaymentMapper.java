package net.likelion.bebc25.itda.payment.mapper;

import net.likelion.bebc25.itda.payment.dto.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PaymentMapper {

    /**
     * 결제 정보 저장
     * payment 테이블에 PS01(결제 대기) 상태로 저장한다
     */
    int insertPayment(Payment payment);

    /**
     * common_code의 PK 조회
     *
     * UNIQUE(type, code) 조건이 있으므로
     * 하나의 코드 ID가 조회된다.
     */
    Long findCommonCodeId(
            @Param("type") int type,
            @Param("code") String code
    );


    /**
     * 결제 완료 후
     * 결제 아이디를 이용해 우리 DB의 결제 정보를 조회
     * @param paymentId 테이블의 pk
     *
     */
    Payment findByPaymentId(@Param("paymentId") String paymentId);

    /**
     * 결제 상품 가격 조회
     * THEME        -> theme.price
     * SUBSCRIPTION -> common_code.numeric_value
     */
    Long findAmount(
            @Param("paymentType") String paymentType,
            @Param("targetId") Long targetId
    );


    /**
     * ✅ 정상 결제로 검증된 payment를 결제 완료 상태로 변경
     *
     * PS01 → PS02
     *
     * transactionId와 실제 결제 완료 시간도 함께 저장한다.
     */
    int updatePaid(

            @Param("paymentId")
            String paymentId,

            @Param("transactionId")
            String transactionId,

            @Param("statusId")
            Long statusId,

            @Param("paidAt")
            LocalDateTime paidAt
    );

    /**
     * 테마 구매이력 저장
     * @param memberId 회원 ID 주문자
     * @param themeId 테마 ID 대상 테마
     * @param paymentId payment 테이블의 pk
     */
    int insertThemePurchase(
            @Param("memberId") Long memberId,
            @Param("themeId") Long themeId,
            @Param("paymentId") Long paymentId
    );

    /**
     * 구독 성공 후 저장
     * @param memberId 회원 ID 주문자
     * @param targetId 구독 대상 회원 번호
     * @param statusId 구독 상태 코드
     */
    int insertSubscription(
            @Param("memberId") Long memberId,
            @Param("targetId") Long targetId,
            @Param("statusId") Long statusId,
            @Param("paymentId") Long paymentId
    );

    /**
     * 회원의 결제 이력 조회
     * @param memberId 회원 ID
     * @return 결제 이력 목록
     */
    List<Payment> findPaymentsByMemberId(
            @Param("memberId") Long memberId
    );
    /**
     * 관리자 전체 결제 이력 조회
     * @return 전체 결제 이력 목록
     */
    List<Payment> findAllPayments();

}
