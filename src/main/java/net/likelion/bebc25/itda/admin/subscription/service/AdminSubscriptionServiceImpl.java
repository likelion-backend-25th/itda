package net.likelion.bebc25.itda.admin.subscription.service;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.admin.subscription.dto.AdminSubscriptionResponse;
import net.likelion.bebc25.itda.admin.subscription.mapper.AdminSubscriptionMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminSubscriptionServiceImpl implements AdminSubscriptionService {

    private final AdminSubscriptionMapper adminSubscriptionMapper;

    @Override
    public List<AdminSubscriptionResponse> getAllSubscriptions() {
        return adminSubscriptionMapper.findAllSubscriptions();
    }
}