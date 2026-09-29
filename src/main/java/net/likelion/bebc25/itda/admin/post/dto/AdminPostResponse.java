package net.likelion.bebc25.itda.admin.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AdminPostResponse {

    private Long id;
    private String nickname;
    private String email;
    private String content;
    private LocalDateTime createdAt;
}