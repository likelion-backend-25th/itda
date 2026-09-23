package net.likelion.bebc25.itda.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SignupRequest {
    String email;
    String password;
    String nickname;
    String profileImage;
    List<Long> interestCategoryIds;
}

