package net.likelion.bebc25.itda.post.service;

import net.likelion.bebc25.itda.post.dto.PostScrapResponse;
import org.apache.ibatis.annotations.Param;

public interface PostScrapService {
    PostScrapResponse toggleScrap(Long memberId, Long postId);
}
