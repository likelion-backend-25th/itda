package net.likelion.bebc25.itda.reply.dto;

import jakarta.validation.constraints.NotBlank;

public record ReplyCreateRequest (
        @NotBlank
        String content
){ }
