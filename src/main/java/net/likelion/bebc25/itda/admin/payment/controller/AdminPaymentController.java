package net.likelion.bebc25.itda.admin.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.admin.payment.dto.AdminPaymentResponse;
import net.likelion.bebc25.itda.admin.payment.dto.AdminRefundResponse;
import net.likelion.bebc25.itda.admin.payment.service.AdminPaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "관리자 결제 API", description = "관리자 결제 관리 기능")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/payments")
public class AdminPaymentController {

    private final AdminPaymentService adminPaymentService;

    @Operation(
            summary = "결제 목록 조회",
            description = "관리자용 전체 결제 목록을 조회한다."
    )
    @GetMapping
    public ResponseEntity<List<AdminPaymentResponse>> getAllPayments() {

        List<AdminPaymentResponse> payments =
                adminPaymentService.getAllPayments();

        return ResponseEntity.ok(payments);
    }

    @Operation(
            summary = "환불 목록 조회",
            description = "payment_refund 환불 내역을 조회한다."
    )
    @GetMapping("/refunds")
    public ResponseEntity<List<AdminRefundResponse>> getAllRefunds() {

        List<AdminRefundResponse> refunds =
                adminPaymentService.getAllRefunds();

        return ResponseEntity.ok(refunds);
    }

    @Operation(
            summary = "환불 승인",
            description = "환불 요청을 승인한다."
    )
    @PatchMapping("/{paymentId}/refund")
    public ResponseEntity<Void> refundPayment(
            @PathVariable Long paymentId
    ) {
        adminPaymentService.refundPayment(paymentId);

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "환불 거절",
            description = "환불 요청을 거절한다."
    )
    @PatchMapping("/{paymentId}/refund/reject")
    public ResponseEntity<Void> rejectRefund(
            @PathVariable Long paymentId
    ) {
        adminPaymentService.rejectRefund(paymentId);

        return ResponseEntity.ok().build();
    }
}
