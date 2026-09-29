package net.likelion.bebc25.itda.member.mapper;

import net.likelion.bebc25.itda.member.dto.FollowingResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FollowingMapper {

    // 특정 회원이 팔로우하는 사람들의 목록 조회
    List<FollowingResponse> findFollowings(@Param("fromId") Long fromId);
}