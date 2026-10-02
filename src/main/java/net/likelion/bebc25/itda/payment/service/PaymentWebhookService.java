package net.likelion.bebc25.itda.payment.service;

public interface PaymentWebhookService {
    /**
     * 브라우저에서 /complete 호출이 누락된 경우에도
     * PortOne이 전달한 paymentId를 이용하여
     * 실제 결제 상태를 확인하고 DB에 반영한다.
     * @param paymentId PortOne Webhook에서 전달받은 결제 ID
     */
    void completePaymentByWebhook(String paymentId);
}
