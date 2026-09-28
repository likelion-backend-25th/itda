package net.likelion.bebc25.itda.admin.member.service;

import net.likelion.bebc25.itda.admin.member.dto.AdminMemberResponse;

import java.util.List;

public interface AdminMemberService {

    List<AdminMemberResponse> getAllMembers();

    void updateMemberStatus(Long memberId, String status);
}