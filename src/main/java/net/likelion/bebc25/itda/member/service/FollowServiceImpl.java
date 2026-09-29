package net.likelion.bebc25.itda.member.service;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.member.mapper.FollowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowServiceImpl implements FollowService {

    private final FollowMapper followMapper;

    @Override
    public void follow(Long fromId, Long toId) {

        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("자기 자신을 팔로우할 수 없습니다.");
        }

        if (followMapper.existsFollow(fromId, toId) > 0) {
            throw new IllegalArgumentException("이미 팔로우한 회원입니다.");
        }

        followMapper.follow(fromId, toId);
    }

    @Override
    public void unfollow(Long fromId, Long toId) {

        if (followMapper.existsFollow(fromId, toId) == 0) {
            throw new IllegalArgumentException("팔로우하지 않은 회원입니다.");
        }

        followMapper.unfollow(fromId, toId);
    }
}