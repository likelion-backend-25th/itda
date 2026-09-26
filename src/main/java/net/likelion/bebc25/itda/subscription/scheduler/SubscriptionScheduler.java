package net.likelion.bebc25.itda.subscription.scheduler;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.subscription.service.SubscriptionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionScheduler {

    private final SubscriptionService subscriptionService;

    @Scheduled(fixedRate = 60000)
    public void expireSubscriptions() {
        subscriptionService.expireSubscriptions();
    }
}