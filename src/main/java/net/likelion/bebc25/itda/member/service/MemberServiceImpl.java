package net.likelion.bebc25.itda.member.service;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.domain.Member;
import net.likelion.bebc25.itda.member.dto.FollowerResponse;
import net.likelion.bebc25.itda.member.dto.FollowingResponse;
import net.likelion.bebc25.itda.member.dto.MemberProfileResponse;
import net.likelion.bebc25.itda.member.dto.SignupRequest;
import net.likelion.bebc25.itda.member.mapper.FollowerMapper;
import net.likelion.bebc25.itda.member.mapper.FollowingMapper;
import net.likelion.bebc25.itda.member.mapper.MemberMapper;
import net.likelion.bebc25.itda.post.mapper.PostMapper;
import net.likelion.bebc25.itda.s3.S3Service;
import net.likelion.bebc25.itda.theme.mapper.ThemeMapper;
import net.likelion.bebc25.itda.theme.service.ThemeService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final FollowerMapper followerMapper;
    private final FollowingMapper followingMapper;
    private final PostMapper postMapper;
    private final PasswordEncoder passwordEncoder;
    private final ThemeService themeService;
    private final S3Service s3Service;

    @Override
    @Transactional
    public void signup(SignupRequest request, MultipartFile profileImage) {
        Member existingMember = memberMapper.findByEmail(request.getEmail());

        if (existingMember != null) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 기본 테마 조회
        Long defaultThemeId = themeService.getDefaultThemeId();

        // 프로필 이미지 S3 업로드
        String profileImageKey = null;

        if (profileImage != null && !profileImage.isEmpty()) {
            profileImageKey = s3Service.upload(profileImage, "profile");
        }

        // 회원 정보 저장
        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .role("ROLE_USER")
                .profileImage(profileImageKey)
                .authmethod("LOCAL")
                .theme_id(defaultThemeId)
                .build();
        memberMapper.save(member);

        // 기본 테마를 보유 테마로 등록
        themeService.insertDefaultTheme(member.getId(), defaultThemeId);
    }

    // 내 팔로워 목록 조회 — DB에는 S3 key만 있으므로 응답 시 프리사인 URL로 변환
    @Override
    public List<FollowerResponse> getFollowers(Long memberId) {
        return followerMapper.findFollowers(memberId).stream()
                .map(follower -> new FollowerResponse(
                        follower.id(),
                        follower.nickname(),
                        s3Service.getPresignedUrl(follower.profileImage())
                ))
                .toList();
    }

    // 팔로잉 목록 조회
    @Override
    public List<FollowingResponse> getFollowings(Long memberId) {
        return followingMapper.findFollowings(memberId).stream()
                .map(following -> new FollowingResponse(
                        following.id(),
                        following.nickname(),
                        s3Service.getPresignedUrl(following.profileImage())
                ))
                .toList();
    }

    @Override
    public MemberProfileResponse getProfile(Member member) {

        Long memberId = member.getId();

        int followerCount = followerMapper.countFollowers(memberId);
        int followingCount = followingMapper.countFollowings(memberId);
        int postCount = postMapper.countPosts(memberId);

        String profileImageUrl = s3Service.getPresignedUrl(member.getProfileImage());

        return MemberProfileResponse.from(
                member,
                profileImageUrl,
                followerCount,
                followingCount,
                postCount
        );
    }

    @Override
    public Member findById(Long memberId) {
        return memberMapper.findById(memberId);
    }
}
