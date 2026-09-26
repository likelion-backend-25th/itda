package net.likelion.bebc25.itda.payment.controller;
import jakarta.validation.Valid;
import net.likelion.bebc25.itda.payment.dto.PaymentCompleteRequest;
import net.likelion.bebc25.itda.payment.dto.PaymentCompleteResponse;
import net.likelion.bebc25.itda.payment.dto.PaymentPrepareRequest;
import net.likelion.bebc25.itda.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.payment.dto.PaymentPrepareResponse;
import net.likelion.bebc25.itda.security.principal.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/prepare")
    public ResponseEntity<PaymentPrepareResponse> preparePayment(
            @RequestBody PaymentPrepareRequest request
    ) {

        // 현재 로그인 회원 정보 연결 전이라면
        // 테스트용 memberId
        Long memberId = 1L;

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
}

