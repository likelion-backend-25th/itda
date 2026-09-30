package net.likelion.bebc25.itda.customer.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.customer.payment.dto.MyPaymentResponse;
import net.likelion.bebc25.itda.customer.payment.service.MyPaymentService;
import net.likelion.bebc25.itda.security.principal.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "내 결제 API", description = "회원 본인 결제 내역 조회")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customer/payments")
public class MyPaymentController {

    private final MyPaymentService myPaymentService;

    @Operation(
            summary = "내 결제 내역 조회",
            description = "로그인한 회원의 결제 내역을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<List<MyPaymentResponse>> getMyPayments(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMember().getId();

        List<MyPaymentResponse> payments =
                myPaymentService.getMyPayments(memberId);

        return ResponseEntity.ok(payments);
    }
}
