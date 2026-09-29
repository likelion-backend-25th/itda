package net.likelion.bebc25.itda.admin.subscription.mapper;

import net.likelion.bebc25.itda.admin.subscription.dto.AdminSubscriptionResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminSubscriptionMapper {

    List<AdminSubscriptionResponse> findAllSubscriptions();

}