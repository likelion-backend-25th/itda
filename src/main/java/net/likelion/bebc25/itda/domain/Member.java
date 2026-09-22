package net.likelion.bebc25.itda.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Member {
    private Long id;
    private String email;
    private String password;
    private String nickname;
    private String profileImage; // s3 URL
    @Builder.Default
    private String role = "ROLE_USER";
    private String introduction;
    private int theme_id; // 현재 적용중인 테마 id
    private String authmethod; // 로그인 인증 방식 (google/kakao/local)
    private int month_income; // 월간 정산 금액
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
}
