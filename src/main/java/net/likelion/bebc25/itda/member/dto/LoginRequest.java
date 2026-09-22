package net.likelion.bebc25.itda.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {
    String email;
    String password;
}

