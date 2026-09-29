package net.likelion.bebc25.itda.member.service;

public interface FollowService {

    // 팔로우
    void follow(Long fromId, Long toId);

    // 언팔로우
    void unfollow(Long fromId, Long toId);
}