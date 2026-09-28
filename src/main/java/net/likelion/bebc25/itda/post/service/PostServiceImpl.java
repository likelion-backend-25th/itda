package net.likelion.bebc25.itda.post.service;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.member.dto.PostUpdateRequest;
import net.likelion.bebc25.itda.post.domain.Post;
import net.likelion.bebc25.itda.post.dto.PostCreateRequest;
import net.likelion.bebc25.itda.post.dto.PostFeedResponse;
import net.likelion.bebc25.itda.post.dto.PostResponse;
import net.likelion.bebc25.itda.post.mapper.PostMapper;
import net.likelion.bebc25.itda.post.mapper.PostReactionMapper;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final PostReactionMapper postReactionMapper;

    // 게시글 등록
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
        // 게시글 등록 직후에 이 게시글에 대해 현재 사용자가 아직 좋아요와 스크랩을 등록하지 않음
        return PostResponse.from(savedPost,false,false);
    }

    // 게시글 단건 조회
    @Override
    @Transactional(rollbackFor =  Exception.class)
    public PostResponse getPostById(Long id, Long memberId, boolean alreadyViewed) {
        Post post = postMapper.findById(id);
        if (post == null) {
            throw new NoSuchElementException("존재하지 않는 게시글입니다. ID: " + id);
        }

        // 조회수 1 증가
        // 처음 본 게시글 일 때만 조회수 증가
        if(!alreadyViewed) {
            postMapper.increaseViewCount(id);
        }

        // 증가된 조회수를 반영하기 위해 다시 조회
        Post updatedPost = postMapper.findById(id);

        boolean liked = false;
        boolean scrapped = false;

        if(memberId != null){
            liked =  postReactionMapper.countLike(memberId, id) > 0;
            scrapped = postReactionMapper.countScrap(memberId, id) > 0;
        }

        return PostResponse.from(updatedPost,liked, scrapped);
    }

    // 게시글 가져오기
    @Override
    public PostFeedResponse getPosts(Long memberId, Long publicCursor, Long subscribedCursor, Long categoryId, int size) {

        // 비로그인시
        if(memberId == null){
            List<Post> publicPosts = postMapper.findPublicPostsByCursor(publicCursor,categoryId, size + 1);
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
                PostResponse response = PostResponse.from(post,false,false);
                responses.add(response);
            }

            return new PostFeedResponse(responses, nextPublicCursor, null, hasNext);
        }

        // 로그인시: 공개글 50% 구독글50%
        int publicSize = size / 2;
        int subscribedSize = size - publicSize;

        // 공개글
        List<Post> publicPosts = postMapper.findPublicPostsByCursor(publicCursor, categoryId, publicSize + 1);
        // 구독글
        List<Post> subscribedPosts = postMapper.findSubscribedPostsByCursor(memberId,subscribedCursor,categoryId, subscribedSize + 1);

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
            boolean liked = postReactionMapper.countLike(memberId, post.getId()) > 0;

            boolean scrapped = postReactionMapper.countScrap(memberId, post.getId()) > 0;
            PostResponse response = PostResponse.from(post,liked, scrapped);
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

    // 게시글 수정
    // 작성자 본인만 수정 가능하게끔
    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("@postServiceImpl.isAuthor(#postId, authentication.principal.id)")
    public PostResponse updatePost(Long memberId, Long postId, PostUpdateRequest request) {

        // 게시글 존재 여부 확인
        Post post = postMapper.findById(postId);

        if(post == null){
            throw new NoSuchElementException("존재하지 않는 게시글입니다. id: " + postId);
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

        // 현재 사용자의 좋아요/스크랩 여부 확인
        boolean liked = postReactionMapper.countLike(memberId, postId) > 0;

        boolean scrapped = postReactionMapper.countScrap(memberId, postId) > 0;

        return PostResponse.from(savedPost, liked, scrapped);

    }

    // 게시글 삭제
    // 관리자랑 해당 글 작성자만 게시글 삭제 가능하게끔
    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('ADMIN') or @postServiceImpl.isAuthor(#postId, authentication.principal.id)")
    public void deletePost(Long postId) {
        Post post = postMapper.findById(postId);

        if(post == null){
            throw new NoSuchElementException("존재하지 않는 게시글입니다. id: " + postId);
        }

        postMapper.deleteById(postId);
    }

    // 게시글 작성자 본인 여부를 검증하는 헬퍼 메서드
    public boolean isAuthor (Long postId, Long memberId){
        Post post = postMapper.findById(postId);

        return post != null && post.getMemberId().equals(memberId);
    }
}
