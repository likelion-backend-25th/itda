package net.likelion.bebc25.itda.payment.webhook;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;

@Component
public class PortOneWebhookVerifier {

    /*
     * webhook-secret: ${PORTONE_WEBHOOK_SECRET}
     * 환경변수에서 값을 가져온다.
     */
    private final String webhookSecret;


    /*
     * 웹훅 요청 시간 허용 범위
     * 너무 오래된 웹훅 요청을 다시 보내는
     * Replay Attack을 방지하기 위해
     * 현재 시간 기준 ±5분까지만 허용한다.
     */
    private static final long TIMESTAMP_TOLERANCE_SECONDS = 300;


    public PortOneWebhookVerifier(
            // applcation.yaml 에서 가져온 키
            @Value("${portone.webhook-secret}") String webhookSecret
    ) {
        this.webhookSecret = webhookSecret;
    }


    /**
     * PortOne 웹훅 서명 검증
     *
     * PortOne은 다음 세 가지 헤더를 전달한다.
     *
     * webhook-id
     * webhook-timestamp
     * webhook-signature
     *
     * 그리고 아래 형식의 문자열을 HMAC-SHA256으로 서명한다.
     *
     * webhook-id.webhook-timestamp.rawBody
     *
     * @param webhookId        PortOne 웹훅 고유 ID
     * @param webhookTimestamp 웹훅 전송 시간
     * @param webhookSignature PortOne에서 생성한 서명
     * @param rawBody          가공하지 않은 원본 JSON Body
     *
     * @return 서명이 정상인 경우 true
     */
    public boolean verify(
            String webhookId,
            String webhookTimestamp,
            String webhookSignature,
            String rawBody
    ) {

        try {

            /*
             * 필수 Header가 하나라도 없는 경우
             * 정상적인 PortOne Webhook 요청으로 볼 수 없다.
             */
            if (webhookId == null || webhookTimestamp == null || webhookSignature == null) {
                return false;
            }


            /*
             * webhook-timestamp는 Unix Timestamp 형식이다.
             * 예:
             * 1759320000
             */
            long timestamp = Long.parseLong(webhookTimestamp);


            /*
             * 현재 시간과 웹훅 요청 시간의 차이를 계산한다.
             */
            long currentTimestamp = Instant.now().getEpochSecond();

            long difference = Math.abs(currentTimestamp - timestamp);


            /*
             * 5분보다 오래된 요청은 거부한다.
             *
             * 웹훅 요청을 탈취한 뒤
             * 동일한 요청을 다시 보내는 공격을 방지하기 위함.
             */
            if (difference > TIMESTAMP_TOLERANCE_SECONDS) {
                return false;
            }


            /*
             * PortOne은 Standard Webhooks 규격을 사용한다.
             *
             * Webhook Secret은 일반적으로
             *
             * whsec_xxxxxxxxx
             *
             * 형태이다.
             *
             * 실제 HMAC 계산에는 whsec_ 뒤의
             * Base64 문자열을 사용한다.
             */
            String secret = webhookSecret;

            if (secret.startsWith("whsec_")) {
                secret = secret.substring("whsec_".length());
            }


            /*
             * Base64로 저장된 Secret을
             * 실제 HMAC Key byte[]로 변환한다.
             */
            byte[] secretBytes = Base64.getDecoder().decode(secret);


            /*
             * PortOne이 서명하는 원문.
             *
             * 매우 중요:
             *
             * JSON을 DTO로 변환했다가 다시 JSON으로 만들면 안 된다.
             *
             * 공백이나 줄바꿈 하나만 달라져도
             * 서명이 달라지기 때문에
             * 반드시 전달받은 rawBody 그대로 사용한다.
             *
             * 형식:
             *
             * webhookId.timestamp.rawBody
             */
            String signedPayload =
                    webhookId
                            + "."
                            + webhookTimestamp
                            + "."
                            + rawBody;


            /*
             * HMAC-SHA256 알고리즘 준비
             */
            Mac mac = Mac.getInstance("HmacSHA256");

            mac.init(
                    new SecretKeySpec(
                            secretBytes,
                            "HmacSHA256"
                    )
            );


            /*
             * 우리 서버에서 직접 계산한 서명
             */
            byte[] expectedSignature =
                    mac.doFinal(
                            signedPayload.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );


            /*
             * webhook-signature Header는
             *
             * v1,xxxxxxxx
             *
             * 형태이며 Secret 교체 과정에서는
             * 여러 개가 들어올 수도 있다.
             *
             * 예:
             *
             * v1,aaaa v1,bbbb
             */
            String[] signatures = webhookSignature.split(" ");


            for (String signature : signatures) {

                /*
                 * "v1,서명값"을
                 *
                 * [0] = v1
                 * [1] = 서명값
                 *
                 * 으로 분리
                 */
                String[] parts = signature.split(",", 2);


                /*
                 * 예상하지 못한 형식은 무시
                 */
                if (parts.length != 2) {
                    continue;
                }


                /*
                 * 현재 HMAC 방식은 v1 서명만 검증
                 */
                if (!"v1".equals(parts[0])) {
                    continue;
                }


                /*
                 * PortOne에서 보내준 Base64 서명을
                 * byte[]로 변환
                 */
                byte[] receivedSignature =
                        Base64.getDecoder().decode(
                                parts[1]
                        );


                /*
                 * 단순 equals 비교 대신
                 * MessageDigest.isEqual()을 사용한다.
                 *
                 * 서명 비교 시간을 이용한
                 * Timing Attack을 줄이기 위한 처리.
                 */
                if (MessageDigest.isEqual(
                        expectedSignature,
                        receivedSignature
                )) {

                    // 정상적인 PortOne Webhook
                    return true;
                }
            }


            /*
             * 전달된 서명 중
             * 일치하는 서명이 하나도 없음
             */
            return false;


        } catch (Exception e) {

            /*
             * timestamp 형식 오류,
             * Base64 변환 오류,
             * HMAC 계산 오류 등
             *
             * 검증 과정에서 문제가 발생하면
             * 안전하게 검증 실패 처리한다.
             */
            return false;
        }
    }
}