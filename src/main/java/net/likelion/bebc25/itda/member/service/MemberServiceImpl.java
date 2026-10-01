package net.likelion.bebc25.itda.member.service;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.domain.Member;
import net.likelion.bebc25.itda.member.dto.*;
import net.likelion.bebc25.itda.member.mapper.FollowerMapper;
import net.likelion.bebc25.itda.member.mapper.FollowingMapper;
import net.likelion.bebc25.itda.commoncode.mapper.CommonCodeMapper;
import net.likelion.bebc25.itda.member.mapper.MemberInterestMapper;
import net.likelion.bebc25.itda.member.mapper.MemberMapper;
import net.likelion.bebc25.itda.post.mapper.PostMapper;
import net.likelion.bebc25.itda.s3.S3Service;
import net.likelion.bebc25.itda.theme.mapper.ThemeMapper;
import net.likelion.bebc25.itda.theme.service.ThemeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.NoSuchElementException;

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
    private final MemberInterestMapper memberInterestMapper;
    private final CommonCodeMapper commonCodeMapper;

    @Override
    @Transactional
    public void signup(SignupRequest request, MultipartFile profileImage) {
        Member existingMember = memberMapper.findByEmail(request.getEmail());

        if (existingMember != null) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        List<Long> interestIds = request.getInterestCategoryIds() == null ? List.of() : request.getInterestCategoryIds().stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (interestIds.isEmpty()) {
            throw new IllegalArgumentException("관심사를 하나 이상 선택해 주세요.");
        }

        for (Long categoryId : interestIds) {
            if (commonCodeMapper.findActivePostCategory(categoryId) == null) {
                throw new IllegalArgumentException("유효하지 않은 관심사입니다.");
            }
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
                .themeId(defaultThemeId)
                .build();
        memberMapper.save(member);

        // 선택한 관심사 → member_interest
        for (Long categoryId : interestIds) {
            memberInterestMapper.insert(member.getId(), categoryId);
        }

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
        List<Long> interestCategoryIds = memberInterestMapper.findCategoryIdsByMemberId(memberId);

        return MemberProfileResponse.from(
                member,
                profileImageUrl,
                followerCount,
                followingCount,
                postCount,
                interestCategoryIds
        );
    }

    @Override
    public Member findById(Long memberId) {
        return memberMapper.findById(memberId);
    }

    @Override
    @Transactional
    public void updateMyProfile(Long memberId, MemberUpdateRequest request, MultipartFile profileImage) {
        Member member = memberMapper.findById(memberId);

        if (member == null) {
            throw new NoSuchElementException("존재하지 않는 회원입니다.");
        }

        String oldKey = member.getProfileImage();
        boolean clear = Boolean.TRUE.equals(request.removeProfileImage());
        String newKey = null;

        // 새 파일이 있으면 교체 우선 (초기화 플래그 무시)
        if (profileImage != null && !profileImage.isEmpty()) {
            newKey = s3Service.upload(profileImage, "profile");
            clear = false;
        }

        int updated = memberMapper.updateMyProfile(
                memberId,
                request.nickname(),
                request.introduction(),
                newKey,
                clear
        );

        if (updated != 1) {
            throw new IllegalStateException("회원 정보 수정에 실패했습니다.");
        }

        // DB 반영 후 옛 S3 객체 정리 (교체 또는 초기화)
        if ((newKey != null || clear) && oldKey != null && !oldKey.isBlank()) {
            if (newKey == null || !oldKey.equals(newKey)) {
                s3Service.delete(oldKey);
            }
        }
    }

    @Override
    @Transactional
    public void updateMyInterests(Long memberId, InterestUpdateRequest request) {
        Member member = memberMapper.findById(memberId);
        if (member == null) {
            throw new NoSuchElementException("존재하지 않는 회원입니다.");
        }

        List<Long> interestIds = request.interestCategoryIds() == null
                ? List.of()
                : request.interestCategoryIds().stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (interestIds.isEmpty()) {
            throw new IllegalArgumentException("관심사를 하나 이상 선택해 주세요.");
        }

        for (Long categoryId : interestIds) {
            if (commonCodeMapper.findActivePostCategory(categoryId) == null) {
                throw new IllegalArgumentException("유효하지 않은 관심사입니다.");
            }
        }

        memberInterestMapper.deleteByMemberId(memberId);
        for (Long categoryId : interestIds) {
            memberInterestMapper.insert(memberId, categoryId);
        }
    }
}
