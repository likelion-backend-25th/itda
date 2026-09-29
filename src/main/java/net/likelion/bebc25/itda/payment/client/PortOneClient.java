package net.likelion.bebc25.itda.payment.client;

import lombok.Getter;
import net.likelion.bebc25.itda.payment.dto.PortOnePaymentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Getter
@Component
public class PortOneClient {

    private final RestClient restClient;
    private final String storeId;
    private final String kakaoPayChannelKey;
    private final String tossPayChannelKey;

    /**
     * PortOne 서버 통신 설정
     *
     * API Secret은 프론트로 전달하지 않고
     * 서버 내부 Authorization 헤더에서만 사용
     */
    public PortOneClient(
            RestClient.Builder builder,
            @Value("${portone.api-secret}") String apiSecret,
            @Value("${portone.store-id}") String storeId,
            @Value("${PORTONE_KAKAOPAY_CHANNEL_KEY}") String kakaoPayChannelKey,
            @Value("${PORTONE_TOSSPAY_CHANNEL_KEY}") String  tossPayChannelKey
    ) {
        this.storeId = storeId;
        this.kakaoPayChannelKey = kakaoPayChannelKey;
        this.tossPayChannelKey = tossPayChannelKey;

        this.restClient = builder
                .baseUrl("https://api.portone.io")
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "PortOne " + apiSecret
                )
                .build();
    }
    /**
     * 결제 정보 사전 등록
     *
     * POST
     * /payments/{paymentId}/pre-register
     */
    public void preRegister(
            String paymentId,
            Long amount
    ){
        PreRegisterRequest request =
                new PreRegisterRequest(
                        storeId,
                        amount
                );

        restClient.post()
                .uri(
                        "/payments/{paymentId}/pre-register",
                        paymentId
                )
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
    /**
     * PortOne API 전용 요청 객체
     *
     * PortOneClient 외부에서는 사용할 필요가 없으므로
     * private record로 처리
     */
    private record PreRegisterRequest(
            String storeId,
            Long totalAmount
    ) {
    }
    public PortOnePaymentResponse getPayment(String paymentId){
        return restClient
                // PortOne V2 결제 단건 조회 API
                .get().uri(
                        "/payments/{paymentId}",
                        paymentId
                )
                // HTTP 요청 실행
                // JSON 응답을 DTO로 변환
                .retrieve().body(
                        PortOnePaymentResponse.class
                );
    }
}
