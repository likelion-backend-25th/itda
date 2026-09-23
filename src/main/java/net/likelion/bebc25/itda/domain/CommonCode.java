package net.likelion.bebc25.itda.domain;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CommonCode {

    private Long id;
    private Integer type;
    private String code;
    private String name;
    private String description;
    private Integer sort;
    private Boolean is_active;
}