package com.bookkok.board.service;

import com.bookkok.board.dto.PostDto;
import com.bookkok.board.entity.Category;
import com.bookkok.board.entity.Post;
import com.bookkok.board.entity.PostLike;
import com.bookkok.board.repository.CategoryRepository;
import com.bookkok.board.repository.PostLikeRepository;
import com.bookkok.board.repository.PostRepository;
import com.bookkok.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final PostLikeRepository postLikeRepository;

    // 게시글 생성
    @Transactional
    public Long createPost(PostDto.CreateRequest request, Member loginMember){
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        Post post = request.toEntity(loginMember, category);

        Post savedPost = postRepository.save(post);
        return savedPost.getPostId();
    }

    // 게시글 전체 조회
    @Transactional(readOnly = true)
    public List<PostDto.ListResponse> getAllPosts() {
        List<Post> posts = postRepository.findAll();

        return posts.stream()
                .map(PostDto.ListResponse::from)
                .collect(Collectors.toList());
    }

    // 게시글 상세 조회
    @Transactional
    public PostDto.DetailResponse getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));

        post.increaseViewCount();

        return PostDto.DetailResponse.from(post);
    }

    // 게시글 수정
    @Transactional
    public PostDto.DetailResponse updatePost(Long postId, PostDto.UpdateRequest request, Member loginMember){
        Post post = postRepository.findById(postId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        if (!post.getAuthorMember().getMemberId().equals(loginMember.getMemberId())) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        post.updatePost(request.getTitle(), request.getContent(), category);

        return PostDto.DetailResponse.from(post);
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long postId, Member loginMember){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        if (!post.getAuthorMember().getMemberId().equals(loginMember.getMemberId())) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        postRepository.delete(post);
    }

    // 게시글 좋아요
    @Transactional
    public void toggleLike(Long postId, Member loginMember){
        Post post = postRepository.findById(postId).orElseThrow();

        Optional<PostLike> postLike = postLikeRepository.findByPostAndMember(post, loginMember);

        if(postLike.isPresent()){
            postLikeRepository.delete(postLike.get());
            post.decreaseLikeCount();
        }else{
            PostLike newLike = PostLike.builder().post(post).member(loginMember).build();
            postLikeRepository.save(newLike);
            post.increaseLikeCount();
        }
    }
}
