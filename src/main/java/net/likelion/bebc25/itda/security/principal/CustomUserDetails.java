package net.likelion.bebc25.itda.security.principal;

import net.likelion.bebc25.itda.domain.Member;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CustomUserDetails implements UserDetails, OAuth2User {

    private final Member member;
    private final Map<String, Object> attributes; // 소셜 프로필 속성 보관 필드 추가

    public CustomUserDetails(Member member){
        this.member = member;
        this.attributes = Collections.emptyMap();
    }

    // 사용자 정보가 전부 들어 있는 Member 객체를 반환
    // OAuth 2.0 소셜 로그인용 신규 생성자 추가
    public CustomUserDetails(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }

    public Member getMember() {
        return member;
    }

    // 회원 id 반환
    public Long getId(){
        return this.member.getId();
    }

    // 권한 목록 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한 문자열을 Spring Security의 SimpleGrantedAuthority 타입으로 변환한다.
        return List.of(new SimpleGrantedAuthority(member.getRole()));
    }

    // 비밀번호를 반환한다.
    @Override
    public @Nullable String getPassword() {
        return member.getPassword();
    }

    // 사용자의 식별자를 반환한다.
    @Override
    public String getUsername() {
        return member.getEmail();
    }

    // 계정 만료 여부
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠김 여부 (비밀번호 5회 연속 틀렸을 경우 등)
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    // 인증 정보 만료 여부 (잘 사용 안함)
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    // 계정 활성화 여부 (휴면 계정 관리)
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    // ----------
    // 2. OAuth2User 인터페이스 구현 메서드 추가
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return member.getEmail();
    }
}