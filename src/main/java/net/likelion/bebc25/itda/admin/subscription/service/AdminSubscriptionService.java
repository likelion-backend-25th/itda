package net.likelion.bebc25.itda.admin.subscription.service;

import net.likelion.bebc25.itda.admin.subscription.dto.AdminSubscriptionResponse;

import java.util.List;

public interface AdminSubscriptionService {

    List<AdminSubscriptionResponse> getAllSubscriptions();

}