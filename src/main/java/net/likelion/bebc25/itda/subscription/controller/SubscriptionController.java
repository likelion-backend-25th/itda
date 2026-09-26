package net.likelion.bebc25.itda.subscription.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.security.principal.CustomUserDetails;
import net.likelion.bebc25.itda.subscription.dto.*;
import net.likelion.bebc25.itda.subscription.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "구독 API", description = "구독 관련 기능")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<Void> subscribe(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody SubscriptionRequest request
    ) {
        Long memberId = userDetails.getMember().getId();

        subscriptionService.validateSubscription(
                memberId,
                request
        );

        return ResponseEntity.ok().build();
    }

    // 자신이 특정 창작자를 구독하고 있는지 여부 (구독중 / 구독하기)
    @GetMapping("/{targetId}")
    public ResponseEntity<SubscriptionStatusResponse> getSubscriptionStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long targetId
    ) {
        Long memberId = userDetails.getMember().getId();

        boolean subscribed =
                subscriptionService.isSubscribed(
                        memberId,
                        targetId
                );

        return ResponseEntity.ok(
                new SubscriptionStatusResponse(subscribed)
        );
    }

    // 내가 구독한 창작자들 목록
    @GetMapping("/me")
    public ResponseEntity<List<MySubscriptionResponse>> getMySubscriptions(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMember().getId();

        List<MySubscriptionResponse> subscriptions =
                subscriptionService.getMySubscriptions(memberId);

        return ResponseEntity.ok(subscriptions);
    }

    // 구독하고 있는 특정 창작자를 구독 해제
    @PatchMapping("/{targetId}/cancel")
    public ResponseEntity<Void> cancelSubscription(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long targetId
    ) {
        Long memberId = userDetails.getMember().getId();

        subscriptionService.cancelSubscription(
                memberId,
                targetId
        );

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{targetId}/count")
    public ResponseEntity<SubscriberCountResponse> getSubscriberCount(
            @PathVariable Long targetId
    ) {
        int count =
                subscriptionService.getSubscriberCount(targetId);

        return ResponseEntity.ok(
                new SubscriberCountResponse(count)
        );
    }

    @GetMapping("/{targetId}/income")
    public ResponseEntity<MonthlyIncomeResponse> getMonthlyIncome(
            @PathVariable Long targetId
    ) {
        int monthlyIncome =
                subscriptionService.getMonthlyIncome(targetId);

        return ResponseEntity.ok(
                new MonthlyIncomeResponse(monthlyIncome)
        );
    }
}