package net.likelion.bebc25.itda.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private int page;          // 1부터
    private int size;
    private long totalElements;
    private int totalPages;
}