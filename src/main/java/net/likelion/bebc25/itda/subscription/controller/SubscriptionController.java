package net.likelion.bebc25.itda.subscription.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.security.principal.CustomUserDetails;
import net.likelion.bebc25.itda.subscription.dto.SubscriptionRequest;
import net.likelion.bebc25.itda.subscription.dto.SubscriptionStatusResponse;
import net.likelion.bebc25.itda.subscription.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
}