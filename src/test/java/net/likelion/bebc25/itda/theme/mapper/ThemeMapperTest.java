package net.likelion.bebc25.itda.theme.mapper;

import net.likelion.bebc25.itda.theme.dto.ThemeResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class ThemeMapperTest {
//
//    @Autowired
//    private ThemeMapper ThemeMapper;
//
//    @Test
//    @DisplayName("좋아요 토글, 카운트 수 변경 테스트")
//    void findAllThemesTest(){
//        // given: 1번 회원이 2번 게시글에 좋아요 시도
//        Long memberId = 1L;
//        Long postId = 2L;
//
//        ThemeResponse beforePost = ThemeMapper.findAllThemes(memberId);
//        int beforeLikeCount = beforePost.likeCount();
//        boolean beforeLiked = postLikeMapper.countLike(memberId, postId) > 0;
//
//        // when: 좋아요 토글
//        LikeToggleResponse result = postLikeService.toggleLike(memberId, postId);
//
//        // then
//        PostResponse afterPost = postMapper.findById(postId);
//        boolean afterLiked = postLikeMapper.countLike(memberId, postId) > 0;
//        if(beforeLiked){ // 토글 이전에 좋아요 상태일 경우라면 토글 이후에는 좋아요 상태가 아니고 좋아요 수는 -1이 되어야 함
//            assertThat(result.liked()).isFalse();
//            assertThat(result.likeCount()).isEqualTo(beforeLikeCount - 1);
//            assertThat(afterLiked).isFalse();
//            assertThat(afterPost.likeCount()).isEqualTo(beforeLikeCount - 1);
//        }else{ // 토글 이전에 좋아요 상태가 아닌 경우라면 토글 이후에는 좋아요 상태가 되고 좋아요 수는 +1이 되어야 함
//            assertThat(result.liked()).isTrue();
//            assertThat(result.likeCount()).isEqualTo(beforeLikeCount + 1);
//            assertThat(afterLiked).isTrue();
//            assertThat(afterPost.likeCount()).isEqualTo(beforeLikeCount + 1);
//        }
//    }
}
