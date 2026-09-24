package net.likelion.bebc25.itda.payment.controller;
import net.likelion.bebc25.itda.payment.dto.PaymentPrepareRequest;
import net.likelion.bebc25.itda.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.payment.dto.PaymentPrepareResponse;
import org.springframework.http.ResponseEntity;
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
}

