package net.likelion.bebc25.itda.theme.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ThemeDetailResponse {
    private Long id;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;
    private ThemeResponse author;

}