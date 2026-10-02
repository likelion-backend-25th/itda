package net.likelion.bebc25.itda.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.likelion.bebc25.itda.payment.dto.*;
import net.likelion.bebc25.itda.payment.service.PaymentRefundService;
import net.likelion.bebc25.itda.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.payment.service.PaymentWebhookService;
import net.likelion.bebc25.itda.payment.webhook.PortOneWebhookVerifier;
import net.likelion.bebc25.itda.security.principal.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Tag(name = "결제 API", description = "PortOne 결제 준비·완료·환불")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRefundService paymentRefundService;
    private final PortOneWebhookVerifier portOneWebhookVerifier;
    private final ObjectMapper objectMapper;
    private final PaymentWebhookService paymentWebhookService;

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
        Long memberId = userDetails.getId();



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
        return paymentRefundService.refundPayment(
                memberId,
                paymentId,
                request
        );
    }
    /*
     * PortOne 서버에서 직접 호출하는 Webhook 엔드포인트.
     *
     * 브라우저의 /complete 요청이 누락되더라도
     * PortOne에서 결제 완료 사실을 서버에 전달할 수 있도록 사용한다.
     *
     * 현재는 웹훅 연결 테스트 단계이므로
     * 요청 수신 확인 후 200 OK만 반환한다.
     */
    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(

            // DTO로 바로 받지 않고 원본 JSON 문자열 그대로 받음
            @RequestBody String rawBody,

            // PortOne 웹훅 서명 검증에 필요한 Header
            @RequestHeader("webhook-id")
            String webhookId,

            // PortOne 웹훅 전송 시간
            @RequestHeader("webhook-timestamp")
            String webhookTimestamp,

            // PortOne에서 전달한 웹훅 서명
            @RequestHeader("webhook-signature")
            String webhookSignature

    ) {

        // PortOne Webhook Secret을 이용하여 서명 검증
        boolean verified = portOneWebhookVerifier.verify(
                        webhookId,
                        webhookTimestamp,
                        webhookSignature,
                        rawBody
                );

        // 서명이 올바르지 않은 요청은 처리하지 않음
        if (!verified) {

            log.warn(
                    "[PORTONE WEBHOOK VERIFICATION FAILED] webhookId={}",
                    webhookId
            );

            return ResponseEntity
                    .badRequest()
                    .build();
        }


        // 2.  서명 검증이 끝난 원본 JSON을 DTO로 변환
        PaymentWebhookRequest webhook;

        try {

            webhook =
                    objectMapper.readValue(
                            rawBody,
                            PaymentWebhookRequest.class
                    );

        } catch (Exception e) {

            // JSON 구조가 잘못되어 DTO 변환에 실패한 경우
            log.warn(
                    "[PORTONE WEBHOOK PARSE FAILED] webhookId={}",
                    webhookId
            );

            return ResponseEntity
                    .badRequest()
                    .build();
        }


        // 3. 실제 수신된 Webhook 데이터 확인
        log.info(
                "[PORTONE WEBHOOK RECEIVED] type={}, paymentId={}, transactionId={}",
                webhook.type(),
                webhook.data().paymentId(),
                webhook.data().transactionId()
        );


        // 4.  결제 완료 이벤트인지 확인
        if (!"Transaction.Paid".equals(webhook.type())) {

            /*
             * 지금은 결제 승인 유실 복구가 목적이므로
             * Transaction.Paid 이벤트만 처리한다.
             *
             * 다른 이벤트는 정상 수신만 하고 종료.
             */
            log.info(
                    "[PORTONE WEBHOOK IGNORED] type={}",
                    webhook.type()
            );

            return ResponseEntity.ok().build();
        }


        // 5. Transaction.Paid 이벤트 확인
        log.info(
                "[PORTONE PAYMENT WEBHOOK] paymentId={}",
                webhook.data().paymentId()
        );
        /*
         * Webhook에서 전달받은 paymentId를 Service로 전달한다.
         *
         * Service에서는:
         *
         * 1. 우리 DB payment 조회
         * 2. 이미 PS02인지 확인
         * 3. PortOne 결제 단건 조회
         * 4. 실제 PAID 상태 확인
         * 5. 결제 금액 검증
         * 6. payment PS01 → PS02 변경
         *
         * 을 처리한다.
         */
        paymentWebhookService.completePaymentByWebhook(webhook.data().paymentId());

        return ResponseEntity.ok().build();
    }
}
