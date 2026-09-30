package net.likelion.bebc25.itda.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MemberUpdateRequest(

        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(max = 50, message = "닉네임은 50자 이하여야 합니다.")
        String nickname,

        @Size(max = 255, message = "소개글은 255자 이하여야 합니다.")
        String introduction,

        /** true면 프로필 이미지를 기본(없음)으로 초기화. 새 파일이 있으면 무시되고 교체 우선 */
        Boolean removeProfileImage
) {}
