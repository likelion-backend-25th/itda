package net.likelion.bebc25.itda.member.dto;

import java.util.List;

public record SignupRequest (
        String email,
        String password,
        String nickname,
        String profileImage,
        List<Long> interestCategoryIds
){}
