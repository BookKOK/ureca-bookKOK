package com.bookkok.board.service;

import com.bookkok.board.dto.CommentDto;
import com.bookkok.board.entity.Comment;
import com.bookkok.board.entity.Post;
import com.bookkok.board.repository.CommentRepository;
import com.bookkok.board.repository.PostRepository;
import com.bookkok.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    // 댓글 작성
    @Transactional
    public Long createComment(CommentDto.CreateRequest request, User loginUser){
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));

        Comment comment = request.toEntity(post, loginUser);

        Comment savedComment = commentRepository.save(comment);
        return savedComment.getCommentId();
    }
    
    // 특정 게시글 댓글 목록 조회
    public List<CommentDto.Response> getCommentsByPost(Long postId){
        if(!postRepository.existsById(postId)){
            throw new IllegalArgumentException("존재하지 않는 게시물입니다.");
        }

        List<Comment> comments = commentRepository.findByPost_PostId(postId);

        return comments.stream()
                .map(CommentDto.Response::from)
                .collect(Collectors.toList());
    }



}
