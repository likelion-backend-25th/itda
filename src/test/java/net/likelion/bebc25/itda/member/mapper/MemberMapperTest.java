package net.likelion.bebc25.itda.member.mapper;

import net.likelion.bebc25.itda.domain.Member;
import net.likelion.bebc25.itda.s3.S3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberMapperTest {

    @Autowired
    private MemberMapper memberMapper;

    @MockitoBean
    private S3Service s3Service;

    @Test
    @DisplayName("findByEmail: 존재하는 이메일이면 회원을 조회한다")
    void findByEmail_exists() {
        Member member = memberMapper.findByEmail("user1@itda.com");

        assertThat(member).isNotNull();
        assertThat(member.getId()).isEqualTo(1L);
        assertThat(member.getEmail()).isEqualTo("user1@itda.com");
        assertThat(member.getNickname()).isEqualTo("책읽는사람");
        assertThat(member.getRole()).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("findByEmail: 없는 이메일이면 null을 반환한다")
    void findByEmail_notFound() {
        Member member = memberMapper.findByEmail("nobody@itda.com");

        assertThat(member).isNull();
    }

    @Test
    @DisplayName("findById: 존재하는 id면 회원을 조회한다")
    void findById_exists() {
        Member member = memberMapper.findById(1L);

        assertThat(member).isNotNull();
        assertThat(member.getEmail()).isEqualTo("user1@itda.com");
        assertThat(member.getProfileImage()).isEqualTo("profile/user1_profile.png");
    }

    @Test
    @DisplayName("findById: 없는 id면 null을 반환한다")
    void findById_notFound() {
        Member member = memberMapper.findById(999_999L);

        assertThat(member).isNull();
    }

    @Test
    @DisplayName("save: 회원을 등록하고 생성된 id를 채운다")
    void save_insertsAndSetsGeneratedId() {
        Member member = Member.builder()
                .email("mapper-test-" + System.nanoTime() + "@itda.com")
                .password("encoded-password")
                .nickname("매퍼테스트")
                .role("ROLE_USER")
                .profileImage("profile/test.png")
                .themeId(1L)
                .authmethod("LOCAL")
                .build();

        int inserted = memberMapper.save(member);

        assertThat(inserted).isEqualTo(1);
        assertThat(member.getId()).isNotNull();

        Member found = memberMapper.findById(member.getId());
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo(member.getEmail());
        assertThat(found.getNickname()).isEqualTo("매퍼테스트");
        assertThat(found.getAuthmethod()).isEqualTo("LOCAL");
        assertThat(found.getThemeId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("updateMyProfile: 닉네임·소개를 수정한다")
    void updateMyProfile_nicknameAndIntroduction() {
        int updated = memberMapper.updateMyProfile(
                1L,
                "수정된닉네임",
                "수정된 소개글",
                null,
                false
        );

        assertThat(updated).isEqualTo(1);

        Member member = memberMapper.findById(1L);
        assertThat(member.getNickname()).isEqualTo("수정된닉네임");
        assertThat(member.getIntroduction()).isEqualTo("수정된 소개글");
        // 이미지 미전달 + clear=false → 기존 이미지 유지
        assertThat(member.getProfileImage()).isEqualTo("profile/user1_profile.png");
    }

    @Test
    @DisplayName("updateMyProfile: clearProfileImage=true면 이미지를 NULL로 만든다")
    void updateMyProfile_clearImage() {
        int updated = memberMapper.updateMyProfile(
                1L,
                "책읽는사람",
                "책과 독서를 좋아합니다.",
                null,
                true
        );

        assertThat(updated).isEqualTo(1);

        Member member = memberMapper.findById(1L);
        assertThat(member.getProfileImage()).isNull();
    }

    @Test
    @DisplayName("updateMyProfile: 새 profileImage가 있으면 교체한다")
    void updateMyProfile_replaceImage() {
        int updated = memberMapper.updateMyProfile(
                1L,
                "책읽는사람",
                "책과 독서를 좋아합니다.",
                "profile/new_user1.png",
                false
        );

        assertThat(updated).isEqualTo(1);

        Member member = memberMapper.findById(1L);
        assertThat(member.getProfileImage()).isEqualTo("profile/new_user1.png");
    }

    @Test
    @DisplayName("updateMonthlyIncome: 월간 수익을 갱신한다")
    void updateMonthlyIncome_success() {
        int updated = memberMapper.updateMonthlyIncome(1L, 12_000);

        assertThat(updated).isEqualTo(1);

        Member member = memberMapper.findById(1L);
        assertThat(member.getMonthIncome()).isEqualTo(12_000);
    }
}
