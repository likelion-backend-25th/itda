package net.likelion.bebc25.itda.mypage.service;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.mypage.dto.MyPagePostResponse;
import net.likelion.bebc25.itda.post.domain.Post;
import net.likelion.bebc25.itda.post.dto.PostResponse;
import net.likelion.bebc25.itda.post.mapper.PostMapper;
import net.likelion.bebc25.itda.post.mapper.PostReactionMapper;
import net.likelion.bebc25.itda.s3.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageServiceImpl implements MyPageService {

    private final PostMapper postMapper;
    private final PostReactionMapper postReactionMapper;
    private final S3Service s3Service;

    private PostResponse toResponse(Post post, boolean liked, boolean scrapped) {
        return PostResponse.from(
                post,
                s3Service.getPresignedUrl(post.getProfileImage()),
                s3Service.getPresignedUrl(post.getImageUrl()),
                liked,
                scrapped
        );
    }

    @Override
    public MyPagePostResponse getMyPosts(Long memberId, Long cursor, int size) {

        // 다음 게시글이 더 있는지 확인하기 위해 size + 1 개 조회
        List<Post> posts = postMapper.findMyPosts(memberId, cursor, size + 1);

        // 실제 요청한 size보다 많이 조회됐다면 다음 데이터가 있음
        boolean hasNext = posts.size() > size;

        // size + 1로 가져온 마지막 제거
        if (hasNext) {
            posts.remove(posts.size() - 1);
        }

        // 다음 요청에 사용할 cursor
        Long nextCursor = null;

        if (!posts.isEmpty()) {
            nextCursor = posts.get(posts.size() - 1).getId();
        }

        // Post를 PostResponse로 반환
        List<PostResponse> postResponses = new ArrayList<>();

        for (Post post : posts) {

            postResponses.add(toResponse(post, false, false ));

        }
        return new MyPagePostResponse(postResponses, nextCursor, hasNext);
    }

    @Override
    public MyPagePostResponse getLikedPosts(Long memberId, Long cursor, int size) {

        // 다음 게시글이 더 있는지 확인하기 위해 size + 1 개 조회
        List<Post> posts = postMapper.findMyLikedPosts(memberId, cursor, size + 1);

        // 실제 요청한 size보다 많이 조회됐다면 다음 데이터가 있음
        boolean hasNext = posts.size() > size;

        // size + 1로 가져온 마지막 제거
        if (hasNext) {
            posts.remove(posts.size() - 1);
        }

        // 다음 요청에 사용할 cursor
        Long nextCursor = null;

        if (!posts.isEmpty()) {
            nextCursor = posts.get(posts.size() - 1).getId();
        }
        // Post를 PostResponse로 반환
        List<PostResponse> responses = new ArrayList<>();

        for (Post post : posts) {
            PostResponse postResponse = toResponse(post, post.isLiked(), post.isScrapped());

            responses.add(postResponse);
        }

        return new MyPagePostResponse(responses, nextCursor, hasNext);
    }

    @Override
    public MyPagePostResponse getScrappedPosts(Long memberId, Long cursor, int size) {

        // 다음 게시글이 더 있는지 확인하기 위해 size + 1 개 조회
        List<Post> posts = postMapper.findMyScrappedPosts(memberId, cursor, size + 1);

        // 실제 요청한 size보다 많이 조회됐다면 다음 데이터가 있음
        boolean hasNext = posts.size() > size;

        // size + 1로 가져온 마지막 제거
        if (hasNext) {
            posts.remove(posts.size() - 1);
        }
        // 다음 요청에 사용할 cursor
        Long nextCursor = null;

        if (!posts.isEmpty()) {
            nextCursor = posts.get(posts.size() - 1).getId();
        }

        // Post를 PostResponse로 반환
        List<PostResponse> responses = new ArrayList<>();

        for (Post post : posts) {
            PostResponse postResponse = toResponse(post, post.isLiked(), post.isScrapped());

            responses.add(postResponse);
        }

        return new MyPagePostResponse(responses, nextCursor, hasNext);
    }

}
