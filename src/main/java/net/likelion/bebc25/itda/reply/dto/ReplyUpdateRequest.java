package net.likelion.bebc25.itda.reply.dto;

import jakarta.validation.constraints.NotBlank;

public record ReplyUpdateRequest (
        @NotBlank
        String content
){ }
