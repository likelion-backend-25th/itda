package net.likelion.bebc25.itda.customer.payment.controller;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.customer.payment.dto.MyPaymentResponse;
import net.likelion.bebc25.itda.customer.payment.service.MyPaymentService;
import net.likelion.bebc25.itda.security.principal.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customer/payments")
public class MyPaymentController {

    private final MyPaymentService myPaymentService;

    @GetMapping
    public ResponseEntity<List<MyPaymentResponse>> getMyPayments(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMember().getId();

        List<MyPaymentResponse> payments =
                myPaymentService.getMyPayments(memberId);

        return ResponseEntity.ok(payments);
    }

    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<Void> requestRefund(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long paymentId
    ) {
        Long memberId = userDetails.getMember().getId();

        myPaymentService.requestRefund(memberId, paymentId);

        return ResponseEntity.ok().build();
    }
}