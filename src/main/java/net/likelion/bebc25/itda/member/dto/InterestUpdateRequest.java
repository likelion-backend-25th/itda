package net.likelion.bebc25.itda.member.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record InterestUpdateRequest(
        @NotEmpty(message = "관심사를 하나 이상 선택해 주세요.")
        List<Long> interestCategoryIds
) {}
