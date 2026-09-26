package net.likelion.bebc25.itda.member.mapper;

import net.likelion.bebc25.itda.member.dto.FollowerResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FollowerMapper {

    // 특정 회원을 팔로우한 사람들의 목록 조회
    List<FollowerResponse> findFollowers(@Param("toId") Long toId);
}