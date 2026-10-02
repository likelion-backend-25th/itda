package net.likelion.bebc25.itda.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "내 프로필 수정 요청")
public record MemberUpdateRequest(

        @Schema(description = "닉네임", example = "책읽는사람")
        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(max = 50, message = "닉네임은 50자 이하여야 합니다.")
        String nickname,

        @Schema(description = "소개글", example = "책과 독서를 좋아합니다.")
        @Size(max = 255, message = "소개글은 255자 이하여야 합니다.")
        String introduction,

        /** true면 프로필 이미지를 기본(없음)으로 초기화. 새 파일이 있으면 무시되고 교체 우선 */
        @Schema(description = "true면 프로필 이미지를 기본(없음)으로 초기화. 새 파일이 있으면 무시되고 교체 우선", example = "false")
        Boolean removeProfileImage
) {}
