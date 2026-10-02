package net.likelion.bebc25.itda.member.service;

import net.likelion.bebc25.itda.commoncode.mapper.CommonCodeMapper;
import net.likelion.bebc25.itda.domain.CommonCode;
import net.likelion.bebc25.itda.domain.Member;
import net.likelion.bebc25.itda.member.dto.FollowerResponse;
import net.likelion.bebc25.itda.member.dto.FollowingResponse;
import net.likelion.bebc25.itda.member.dto.InterestUpdateRequest;
import net.likelion.bebc25.itda.member.dto.MemberProfileResponse;
import net.likelion.bebc25.itda.member.dto.MemberUpdateRequest;
import net.likelion.bebc25.itda.member.dto.SignupRequest;
import net.likelion.bebc25.itda.member.mapper.FollowerMapper;
import net.likelion.bebc25.itda.member.mapper.FollowingMapper;
import net.likelion.bebc25.itda.member.mapper.MemberInterestMapper;
import net.likelion.bebc25.itda.member.mapper.MemberMapper;
import net.likelion.bebc25.itda.post.mapper.PostMapper;
import net.likelion.bebc25.itda.s3.S3Service;
import net.likelion.bebc25.itda.theme.service.ThemeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock private MemberMapper memberMapper;
    @Mock private FollowerMapper followerMapper;
    @Mock private FollowingMapper followingMapper;
    @Mock private PostMapper postMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private ThemeService themeService;
    @Mock private S3Service s3Service;
    @Mock private MemberInterestMapper memberInterestMapper;
    @Mock private CommonCodeMapper commonCodeMapper;

    @InjectMocks
    private MemberServiceImpl memberService;

    private SignupRequest signupRequest(String email, String password, String nickname, List<Long> interests) {
        SignupRequest request = new SignupRequest();
        ReflectionTestUtils.setField(request, "email", email);
        ReflectionTestUtils.setField(request, "password", password);
        ReflectionTestUtils.setField(request, "nickname", nickname);
        ReflectionTestUtils.setField(request, "interestCategoryIds", interests);
        return request;
    }

    @Test
    @DisplayName("signup: 성공 시 회원·관심사·기본 테마를 저장한다")
    void signup_success() {
        SignupRequest request = signupRequest("new@itda.com", "pw1234", "닉네임", List.of(1L, 2L));
        MockMultipartFile image = new MockMultipartFile(
                "profileImage", "a.png", "image/png", "img".getBytes()
        );

        given(memberMapper.findByEmail("new@itda.com")).willReturn(null);
        given(commonCodeMapper.findActivePostCategory(1L)).willReturn(CommonCode.builder().id(1L).build());
        given(commonCodeMapper.findActivePostCategory(2L)).willReturn(CommonCode.builder().id(2L).build());
        given(themeService.getDefaultThemeId()).willReturn(3L);
        given(passwordEncoder.encode("pw1234")).willReturn("encoded-pw");
        given(s3Service.upload(image, "profile")).willReturn("profile/a.png");
        doAnswer(invocation -> {
            Member saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 100L);
            return 1;
        }).when(memberMapper).save(any(Member.class));

        memberService.signup(request, image);

        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberMapper).save(memberCaptor.capture());
        Member saved = memberCaptor.getValue();
        assertThat(saved.getEmail()).isEqualTo("new@itda.com");
        assertThat(saved.getPassword()).isEqualTo("encoded-pw");
        assertThat(saved.getNickname()).isEqualTo("닉네임");
        assertThat(saved.getRole()).isEqualTo("ROLE_USER");
        assertThat(saved.getAuthmethod()).isEqualTo("LOCAL");
        assertThat(saved.getThemeId()).isEqualTo(3L);
        assertThat(saved.getProfileImage()).isEqualTo("profile/a.png");

        verify(memberInterestMapper).insert(100L, 1L);
        verify(memberInterestMapper).insert(100L, 2L);
        verify(themeService).insertDefaultTheme(100L, 3L);
    }

    @Test
    @DisplayName("signup: 이미 가입된 이메일이면 예외")
    void signup_duplicateEmail() {
        SignupRequest request = signupRequest("dup@itda.com", "pw", "닉", List.of(1L));
        given(memberMapper.findByEmail("dup@itda.com"))
                .willReturn(Member.builder().id(1L).email("dup@itda.com").build());

        assertThatThrownBy(() -> memberService.signup(request, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 가입된 이메일입니다.");

        verify(memberMapper, never()).save(any());
    }

    @Test
    @DisplayName("signup: 관심사 미선택이면 예외")
    void signup_emptyInterests() {
        SignupRequest request = signupRequest("a@itda.com", "pw", "닉", List.of());
        given(memberMapper.findByEmail("a@itda.com")).willReturn(null);

        assertThatThrownBy(() -> memberService.signup(request, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("관심사를 하나 이상 선택해 주세요.");
    }

    @Test
    @DisplayName("signup: 유효하지 않은 관심사이면 예외")
    void signup_invalidInterest() {
        SignupRequest request = signupRequest("a@itda.com", "pw", "닉", List.of(99L));
        given(memberMapper.findByEmail("a@itda.com")).willReturn(null);
        given(commonCodeMapper.findActivePostCategory(99L)).willReturn(null);

        assertThatThrownBy(() -> memberService.signup(request, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 관심사입니다.");
    }

    @Test
    @DisplayName("getFollowers: S3 key를 프리사인 URL로 변환한다")
    void getFollowers_presignsProfileImage() {
        given(followerMapper.findFollowers(1L)).willReturn(List.of(
                new FollowerResponse(2L, "팔로워1", "profile/key1")
        ));
        given(s3Service.getPresignedUrl("profile/key1"))
                .willReturn("https://cdn.example/profile/key1");

        List<FollowerResponse> result = memberService.getFollowers(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(2L);
        assertThat(result.get(0).nickname()).isEqualTo("팔로워1");
        assertThat(result.get(0).profileImage()).isEqualTo("https://cdn.example/profile/key1");
    }

    @Test
    @DisplayName("getFollowings: S3 key를 프리사인 URL로 변환한다")
    void getFollowings_presignsProfileImage() {
        given(followingMapper.findFollowings(1L)).willReturn(List.of(
                new FollowingResponse(3L, "팔로잉1", "profile/key2")
        ));
        given(s3Service.getPresignedUrl("profile/key2"))
                .willReturn("https://cdn.example/profile/key2");

        List<FollowingResponse> result = memberService.getFollowings(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).profileImage()).isEqualTo("https://cdn.example/profile/key2");
    }

    @Test
    @DisplayName("getProfile: 집계·관심사·이미지 URL을 포함한 프로필을 반환한다")
    void getProfile_success() {
        Member member = Member.builder()
                .id(1L)
                .email("user@itda.com")
                .nickname("잇다")
                .profileImage("profile/me")
                .role("ROLE_USER")
                .introduction("소개")
                .themeId(3L)
                .status("ACTIVE")
                .build();

        given(followerMapper.countFollowers(1L)).willReturn(5);
        given(followingMapper.countFollowings(1L)).willReturn(7);
        given(postMapper.countPosts(1L)).willReturn(3);
        given(s3Service.getPresignedUrl("profile/me")).willReturn("https://cdn.example/me");
        given(memberInterestMapper.findCategoryIdsByMemberId(1L)).willReturn(List.of(1L, 2L));

        MemberProfileResponse profile = memberService.getProfile(member);

        assertThat(profile.id()).isEqualTo(1L);
        assertThat(profile.email()).isEqualTo("user@itda.com");
        assertThat(profile.profileImage()).isEqualTo("https://cdn.example/me");
        assertThat(profile.followerCount()).isEqualTo(5);
        assertThat(profile.followingCount()).isEqualTo(7);
        assertThat(profile.postCount()).isEqualTo(3);
        assertThat(profile.interestCategoryIds()).containsExactly(1L, 2L);
    }

    @Test
    @DisplayName("findById: Mapper 결과를 그대로 반환한다")
    void findById_delegates() {
        Member member = Member.builder().id(1L).email("a@itda.com").build();
        given(memberMapper.findById(1L)).willReturn(member);

        assertThat(memberService.findById(1L)).isSameAs(member);
    }

    @Test
    @DisplayName("updateMyProfile: 새 이미지 업로드 시 교체하고 옛 키를 삭제한다")
    void updateMyProfile_replaceImage() {
        Member member = Member.builder()
                .id(1L)
                .profileImage("profile/old.png")
                .build();
        MemberUpdateRequest request = new MemberUpdateRequest("새닉", "새소개", true);
        MockMultipartFile image = new MockMultipartFile(
                "profileImage", "new.png", "image/png", "x".getBytes()
        );

        given(memberMapper.findById(1L)).willReturn(member);
        given(s3Service.upload(image, "profile")).willReturn("profile/new.png");
        given(memberMapper.updateMyProfile(1L, "새닉", "새소개", "profile/new.png", false))
                .willReturn(1);

        memberService.updateMyProfile(1L, request, image);

        verify(s3Service).delete("profile/old.png");
    }

    @Test
    @DisplayName("updateMyProfile: 이미지 초기화 시 옛 키를 삭제한다")
    void updateMyProfile_clearImage() {
        Member member = Member.builder()
                .id(1L)
                .profileImage("profile/old.png")
                .build();
        MemberUpdateRequest request = new MemberUpdateRequest("닉", "소개", true);

        given(memberMapper.findById(1L)).willReturn(member);
        given(memberMapper.updateMyProfile(1L, "닉", "소개", null, true)).willReturn(1);

        memberService.updateMyProfile(1L, request, null);

        verify(s3Service, never()).upload(any(), anyString());
        verify(s3Service).delete("profile/old.png");
    }

    @Test
    @DisplayName("updateMyProfile: 회원이 없으면 예외")
    void updateMyProfile_memberNotFound() {
        given(memberMapper.findById(1L)).willReturn(null);

        assertThatThrownBy(() -> memberService.updateMyProfile(
                1L, new MemberUpdateRequest("닉", null, false), null
        ))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    @DisplayName("updateMyProfile: DB 수정 실패 시 예외")
    void updateMyProfile_updateFailed() {
        given(memberMapper.findById(1L)).willReturn(Member.builder().id(1L).build());
        given(memberMapper.updateMyProfile(anyLong(), anyString(), any(), isNull(), anyBoolean()))
                .willReturn(0);

        assertThatThrownBy(() -> memberService.updateMyProfile(
                1L, new MemberUpdateRequest("닉", "소개", false), null
        ))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("회원 정보 수정에 실패했습니다.");
    }

    @Test
    @DisplayName("updateMyInterests: 기존 관심사 삭제 후 재등록한다")
    void updateMyInterests_success() {
        given(memberMapper.findById(1L)).willReturn(Member.builder().id(1L).build());
        given(commonCodeMapper.findActivePostCategory(1L)).willReturn(CommonCode.builder().id(1L).build());
        given(commonCodeMapper.findActivePostCategory(2L)).willReturn(CommonCode.builder().id(2L).build());

        memberService.updateMyInterests(1L, new InterestUpdateRequest(List.of(1L, 2L, 2L)));

        verify(memberInterestMapper).deleteByMemberId(1L);
        verify(memberInterestMapper).insert(1L, 1L);
        verify(memberInterestMapper).insert(1L, 2L);
    }

    @Test
    @DisplayName("updateMyInterests: 관심사 비어 있으면 예외")
    void updateMyInterests_empty() {
        given(memberMapper.findById(1L)).willReturn(Member.builder().id(1L).build());

        assertThatThrownBy(() -> memberService.updateMyInterests(1L, new InterestUpdateRequest(List.of())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("관심사를 하나 이상 선택해 주세요.");

        verify(memberInterestMapper, never()).deleteByMemberId(anyLong());
    }
}
