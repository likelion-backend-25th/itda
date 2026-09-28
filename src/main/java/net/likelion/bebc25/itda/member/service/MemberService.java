package net.likelion.bebc25.itda.member.service;

import net.likelion.bebc25.itda.domain.Member;
import net.likelion.bebc25.itda.member.dto.FollowingResponse;
import net.likelion.bebc25.itda.member.dto.FollowerResponse;
import net.likelion.bebc25.itda.member.dto.MemberProfileResponse;
import net.likelion.bebc25.itda.member.dto.SignupRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MemberService {

    void signup(SignupRequest request, MultipartFile profileImage);

    List<FollowerResponse> getFollowers(Long memberId);

    List<FollowingResponse> getFollowings(Long memberId);

    MemberProfileResponse getProfile(Member member);

    Member findById(Long memberId);
}
