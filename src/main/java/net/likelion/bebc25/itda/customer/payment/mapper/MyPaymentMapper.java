package net.likelion.bebc25.itda.customer.payment.mapper;

import net.likelion.bebc25.itda.customer.payment.dto.MyPaymentResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MyPaymentMapper {

    List<MyPaymentResponse> findMyPayments(
            @Param("memberId") Long memberId
    );

    int requestRefund(
            @Param("memberId") Long memberId,
            @Param("paymentId") Long paymentId
    );
}