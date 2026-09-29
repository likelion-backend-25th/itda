package net.likelion.bebc25.itda.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FollowMapper {

    // 팔로우 여부 확인
    int existsFollow(
            @Param("fromId") Long fromId,
            @Param("toId") Long toId
    );

    // 팔로우
    void follow(
            @Param("fromId") Long fromId,
            @Param("toId") Long toId
    );

    // 언팔로우
    void unfollow(
            @Param("fromId") Long fromId,
            @Param("toId") Long toId
    );
}