package net.likelion.bebc25.itda.member.service;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.domain.Member;
import net.likelion.bebc25.itda.member.dto.FollowerResponse;
import net.likelion.bebc25.itda.member.dto.SignupRequest;
import net.likelion.bebc25.itda.member.mapper.FollowerMapper;
import net.likelion.bebc25.itda.member.mapper.MemberMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberMapper memberMapper;
    private final FollowerMapper followerMapper;
    private final PasswordEncoder passwordEncoder;

    public void signup(SignupRequest request) {
        Member existingMember = memberMapper.findByEmail(request.getEmail());
        if (existingMember != null) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .role("ROLE_USER")
                .authmethod("LOCAL")
                .theme_id(1)
                .build();

        memberMapper.save(member);
    }

    // 내 팔로워 목록 조회
    public List<FollowerResponse> getFollowers(Long memberId) {
        return followerMapper.findFollowers(memberId);
    }
}