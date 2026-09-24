package net.likelion.bebc25.itda.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public record LoginRequest (
        String email,
        String password
){}
