package net.likelion.bebc25.itda.member.service;

import net.likelion.bebc25.itda.domain.Member;
import net.likelion.bebc25.itda.member.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MemberService {

    void signup(SignupRequest request, MultipartFile profileImage);

    List<FollowerResponse> getFollowers(Long memberId);

    List<FollowingResponse> getFollowings(Long memberId);

    MemberProfileResponse getProfile(Member member);

    Member findById(Long memberId);

    // 회원 정보 수정
    void updateMyProfile(Long memberId, MemberUpdateRequest request, MultipartFile profileImage);

    // 내 관심사 수정 (전체 교체)
    void updateMyInterests(Long memberId, InterestUpdateRequest request);
}
