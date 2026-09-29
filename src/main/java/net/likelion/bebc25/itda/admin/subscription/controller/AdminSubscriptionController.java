package net.likelion.bebc25.itda.admin.subscription.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.admin.subscription.dto.AdminSubscriptionResponse;
import net.likelion.bebc25.itda.admin.subscription.service.AdminSubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "관리자 구독 API", description = "관리자 구독 관리 기능")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/subscriptions")
public class AdminSubscriptionController {

    private final AdminSubscriptionService adminSubscriptionService;

    @GetMapping
    public ResponseEntity<List<AdminSubscriptionResponse>> getAllSubscriptions() {

        List<AdminSubscriptionResponse> subscriptions =
                adminSubscriptionService.getAllSubscriptions();

        return ResponseEntity.ok(subscriptions);
    }
}