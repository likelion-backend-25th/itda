package net.likelion.bebc25.itda.admin.member.mapper;

import net.likelion.bebc25.itda.admin.member.dto.AdminMemberResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminMemberMapper {

    List<AdminMemberResponse> findAllMembers();

    // 회원 상태 수정
    int updateMemberStatus(
            @Param("memberId") Long memberId,
            @Param("status") String status
    );
}