package net.likelion.bebc25.itda.member.mapper;

import net.likelion.bebc25.itda.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class MemberMapperTest {

//    @Autowired
//    private MemberMapper memberMapper;
//
//    @Test
//    @DisplayName("email로 유저 조회")
//    void findByEmailTest() {
//        // given
//        String email = "user1@itda.com";
//
//        // when
//        Member member = memberMapper.findByEmail(email);
//
//        // then
//        assertThat(member).isNotNull();
//        assertThat(member.getEmail()).isEqualTo(email);
//    }
}
