package net.likelion.bebc25.itda.member.service;

import net.likelion.bebc25.itda.member.dto.FollowerResponse;
import net.likelion.bebc25.itda.member.dto.SignupRequest;

import java.util.List;

public interface MemberService {

    void signup(SignupRequest request);

    List<FollowerResponse> getFollowers(Long memberId);
}
