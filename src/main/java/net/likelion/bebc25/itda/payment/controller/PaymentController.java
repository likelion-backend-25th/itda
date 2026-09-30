package net.likelion.bebc25.itda.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.likelion.bebc25.itda.payment.dto.*;
import net.likelion.bebc25.itda.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.security.principal.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Tag(name = "결제 API", description = "PortOne 결제 준비·완료·환불")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(
            summary = "결제 준비",
            description = "PortOne 결제에 필요한 paymentId와 결제 정보를 준비한다."
    )
    @PostMapping("/prepare")
    public ResponseEntity<PaymentPrepareResponse> preparePayment(
            // 현재 로그인 사용자
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PaymentPrepareRequest request
    ) {

        //  WT 로그인 사용자 ID 사용
        Long memberId = userDetails.getId();

        PaymentPrepareResponse response =
                paymentService.preparePayment(
                        memberId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    /**
     * 결제 완료 검증 API
     *
     * 프론트에서 PortOne 결제가 성공한 후
     * paymentId를 전달하면
     *
     * 백엔드가 PortOne 서버에 실제 결제 정보를 조회하고
     * 금액/상태를 검증한 뒤
     * 우리 DB를 결제 완료 상태로 변경한다.
     */
    @Operation(
            summary = "결제 완료 검증",
            description = "PortOne 결제 성공 후 paymentId를 검증하고 DB 결제 상태를 완료로 변경한다."
    )
    @PostMapping("/complete")
    public ResponseEntity<PaymentCompleteResponse> completePayment(

            // 현재 로그인 사용자 정보
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            // 프론트에서 전달한 paymentId
            @Valid
            @RequestBody
            PaymentCompleteRequest request
    ) {

        /*
         * JWT에 저장되어 있는 현재 로그인 회원 ID
         */
        Long memberId =
                userDetails.getId();


        /*
         * 결제 완료 검증 수행
         */
        PaymentCompleteResponse response =
                paymentService.completePayment(
                        memberId,
                        request
                );


        /*
         * 정상적으로 완료되면 HTTP 200 반환
         */
        return ResponseEntity.ok(response);
    }
    //
    @Operation(
            summary = "결제 환불 요청",
            description = "완료된 결제에 대해 환불을 요청한다."
    )
    @PostMapping("/refund/{paymentId}")
    public PaymentRefundResponse refundPayment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String paymentId,
            @RequestBody PaymentRefundRequest request
    ) {

        // 현재 로그인한 회원 ID
        Long memberId = userDetails.getId();

        // 실제 환불 처리는 PaymentServiceImpl에서 진행
        return paymentService.refundPayment(
                memberId,
                paymentId,
                request
        );
    }
}
