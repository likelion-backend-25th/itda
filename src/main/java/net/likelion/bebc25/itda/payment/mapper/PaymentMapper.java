package net.likelion.bebc25.itda.payment.mapper;

import net.likelion.bebc25.itda.payment.dto.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface PaymentMapper {

    /**
     * 결제 정보 저장
     * payment 테이블에 PS01(결제 대기) 상태로 저장한다
     */
    int insert(Payment payment);

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
     * @param paymentId
     *
     */
    Payment findByPaymentId(@Param("paymentId") String paymentId);

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
}
