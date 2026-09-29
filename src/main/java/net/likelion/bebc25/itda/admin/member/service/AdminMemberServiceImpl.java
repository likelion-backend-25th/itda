package net.likelion.bebc25.itda.admin.member.service;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.admin.member.dto.AdminMemberResponse;
import net.likelion.bebc25.itda.admin.member.mapper.AdminMemberMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMemberServiceImpl implements AdminMemberService {

    private final AdminMemberMapper adminMemberMapper;

    @Override
    public List<AdminMemberResponse> getAllMembers() {
        return adminMemberMapper.findAllMembers();
    }

    @Override
    @Transactional
    public void updateMemberStatus(Long memberId, String status) {

        if (!status.equals("ACTIVE") && !status.equals("SUSPENDED")) {
            throw new IllegalArgumentException("올바르지 않은 회원 상태입니다.");
        }

        int updatedCount = adminMemberMapper.updateMemberStatus(memberId, status);

        if (updatedCount == 0) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }
    }
}