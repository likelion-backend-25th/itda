package net.likelion.bebc25.itda.security.service;

import net.likelion.bebc25.itda.domain.Member;
import net.likelion.bebc25.itda.member.mapper.MemberMapper;
import net.likelion.bebc25.itda.security.principal.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberMapper memberMapper;

    public CustomUserDetailsService(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberMapper.findByEmail(email);
        if(member == null){
            throw new UsernameNotFoundException("해당 이메일을 사용자를 찾을 수 없습니다. : " + email);
        }
        return new CustomUserDetails(member);
    }

    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        Member member = memberMapper.findById(id);
        if(member == null){
            throw new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다. : " + id);
        }
        return new CustomUserDetails(member);
    }
}
