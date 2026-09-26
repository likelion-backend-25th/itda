package net.likelion.bebc25.itda.payment.mapper;

import net.likelion.bebc25.itda.payment.dto.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    // 나중에 complete 에서 사용
    Payment findByPaymentId(String paymentId);
}
