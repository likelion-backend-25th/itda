package net.likelion.bebc25.itda.admin.payment.controller;

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

    @GetMapping
    public ResponseEntity<List<AdminPaymentResponse>> getAllPayments() {

        List<AdminPaymentResponse> payments =
                adminPaymentService.getAllPayments();

        return ResponseEntity.ok(payments);
    }

    @GetMapping("/refunds")
    public ResponseEntity<List<AdminRefundResponse>> getAllRefunds() {

        List<AdminRefundResponse> refunds =
                adminPaymentService.getAllRefunds();

        return ResponseEntity.ok(refunds);
    }

    @PatchMapping("/{paymentId}/refund")
    public ResponseEntity<Void> refundPayment(
            @PathVariable Long paymentId
    ) {
        adminPaymentService.refundPayment(paymentId);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{paymentId}/refund/reject")
    public ResponseEntity<Void> rejectRefund(
            @PathVariable Long paymentId
    ) {
        adminPaymentService.rejectRefund(paymentId);

        return ResponseEntity.ok().build();
    }
}