package net.likelion.bebc25.itda.post.service;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.member.dto.PostUpdateRequest;
import net.likelion.bebc25.itda.post.domain.Post;
import net.likelion.bebc25.itda.post.dto.PostCreateRequest;
import net.likelion.bebc25.itda.post.dto.PostFeedResponse;
import net.likelion.bebc25.itda.post.dto.PostResponse;
import net.likelion.bebc25.itda.post.mapper.PostMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor    // finale이나 @NonNull 필드들을 자동으로 생성자를 만들어준다
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;

    @Override
    @Transactional
    public PostResponse createPost(Long memberId, PostCreateRequest request) {
        Post post = Post.builder()
                .memberId(memberId)
                .categoryId(request.categoryId())
                .content(request.content())
                .imageUrl(request.imageUrl())
                .subscriberOnly(request.subscriberOnly())
                .build();

        postMapper.save(post);
        Post savedPost = postMapper.findById(post.getId());
        return PostResponse.from(savedPost);
    }

    @Override
    public PostResponse getPostById(Long id) {
        Post post = postMapper.findById(id);
        if (post == null) {
            throw new NoSuchElementException("존재하지 않는 게시글입니다. ID: " + id);
        }
        return PostResponse.from(post);
    }

    @Override
    public PostFeedResponse getPosts(Long memberId, Long publicCursor, Long subscribedCursor, int size) {

        // 비로그인시
        if(memberId == null){
            List<Post> publicPosts = postMapper.findPublicPostsByCursor(publicCursor,size + 1);
            // size보다 많이 조회됐다면 다음 게시글이 있다는 걸 확인
            boolean hasNext = publicPosts.size() > size;

            // 더 가져온 1개는 이번 응답에선 제외
            if(hasNext){
                publicPosts.remove(publicPosts.size() - 1);
            }

            Long nextPublicCursor = publicPosts.isEmpty() ? null : publicPosts.get(publicPosts.size()-1).getId();

            // Post를 PostResponse로 변환한다.
            List<PostResponse> responses = new ArrayList<>();
            for (Post post : publicPosts) {
                PostResponse response = PostResponse.from(post);
                responses.add(response);
            }

            return new PostFeedResponse(responses, nextPublicCursor, null, hasNext);
        }

        // 로그인시: 공개글 50% 구독글50%
        int publicSize = size / 2;
        int subscribedSize = size - publicSize;

        // 공개글
        List<Post> publicPosts = postMapper.findPublicPostsByCursor(publicCursor,publicSize + 1);
        // 구독글
        List<Post> subscribedPosts = postMapper.findSubscribedPostsByCursor(memberId,subscribedCursor,subscribedSize + 1);

        // 각각 다음 데이터가 있는지 판단
        boolean hasNextPublic = publicPosts.size() > publicSize;

        boolean hasNextSubscribed = subscribedPosts.size() > subscribedSize;

        // 확인용으로 더 가져온 마지막 1개 제거
        if (hasNextPublic) {
            publicPosts.remove(publicPosts.size() - 1);
        }

        if (hasNextSubscribed) {
            subscribedPosts.remove(subscribedPosts.size() - 1);
        }

        Long nextPublicCursor = publicPosts.isEmpty() ? null : publicPosts.get(publicPosts.size() - 1).getId();
        Long nextSubscribedCursor = subscribedPosts.isEmpty() ? null : subscribedPosts.get(subscribedPosts.size()-1).getId();

        // 공개글  + 구독글 합치기
        List<Post> posts = new ArrayList<>();

        posts.addAll(publicPosts);
        posts.addAll(subscribedPosts);

        // createdAt 순으로 가져온뒤 최신순으로 정렬
        posts.sort(Comparator.comparing(Post::getCreatedAt).reversed());

        List<PostResponse> responses = new ArrayList<>();
        // posts에서 Post하나를 꺼내고 PostResponse.from(post)로 변환
        // responses 리스트에 추가
        for(Post post : posts) {
            PostResponse response = PostResponse.from(post);
            responses.add(response);
        }

        // 공개글이나 구독글 둘 중 하나라도 더 있으면 true
        boolean hasNext = hasNextPublic || hasNextSubscribed;

        return new PostFeedResponse(
                responses,
                nextPublicCursor,
                nextSubscribedCursor,
                hasNext
        );

    }

    @Override
    @Transactional
    public PostResponse updatePost(Long memberId, Long postId, PostUpdateRequest request) {

        // 게시글 존재 여부 확인
        Post post = postMapper.findById(postId);

        if(post == null){
            throw new NoSuchElementException("존재하지 않는 게시글입니다. id: " + postId);
        }

        // 작성자 본인인지 확인
        if(!post.getMemberId().equals(memberId)){
            throw new IllegalArgumentException("본인이 작성한 게시글만 수정할 수 있습니다.");
        }

        // 수정할 값으로 Post 객체 생성
        Post updatedPost = Post.builder()
                .id(postId)
                .memberId(memberId)
                .categoryId(request.categoryId())
                .content(request.content())
                .imageUrl(request.imageUrl())
                .subscriberOnly(request.subscriberOnly())
                .build();

        // DB 수정
        postMapper.update(updatedPost);

        // 수정된 게시글 다시 조회
        Post savedPost = postMapper.findById(postId);

        return PostResponse.from(savedPost);

    }
}
