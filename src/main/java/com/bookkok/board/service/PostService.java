package com.bookkok.board.service;

import com.bookkok.board.dto.PostDto;
import com.bookkok.board.entity.Category;
import com.bookkok.board.entity.Post;
import com.bookkok.board.repository.CategoryRepository;
import com.bookkok.board.repository.PostRepository;
import com.bookkok.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;

    // 게시글 생성
    @Transactional
    public Long createPost(PostDto.CreateRequest request, User loginUser){
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        Post post = request.toEntity(loginUser, category);

        Post savedPost = postRepository.save(post);
        return savedPost.getPostId();
    }

    // 게시글 전체 조회
    public List<PostDto.ListResponse> getAllPosts() {
        List<Post> posts = postRepository.findAll();

        return posts.stream()
                .map(PostDto.ListResponse::from)
                .collect(Collectors.toList());
    }

    // 게시글 상세 조회
    public PostDto.DetailResponse getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));

        return PostDto.DetailResponse.from(post);
    }
}
