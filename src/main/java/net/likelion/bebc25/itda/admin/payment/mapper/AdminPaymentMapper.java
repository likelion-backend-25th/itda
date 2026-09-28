package net.likelion.bebc25.itda.admin.payment.mapper;

import net.likelion.bebc25.itda.admin.payment.dto.AdminPaymentResponse;
import net.likelion.bebc25.itda.admin.payment.dto.AdminRefundResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminPaymentMapper {

    List<AdminPaymentResponse> findAllPayments();

    List<AdminRefundResponse> findAllRefunds();

    int refundPayment(Long paymentId);
}