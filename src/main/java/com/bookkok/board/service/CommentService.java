package com.bookkok.board.service;

import com.bookkok.board.dto.CommentDto;
import com.bookkok.board.entity.Comment;
import com.bookkok.board.entity.Post;
import com.bookkok.board.repository.CommentRepository;
import com.bookkok.board.repository.PostRepository;
import com.bookkok.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sound.midi.MetaMessage;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    // 댓글 작성
    @Transactional
    public Long createComment(CommentDto.CreateRequest request, Member loginMember){
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));

        Comment comment = request.toEntity(post, loginMember);

        Comment savedComment = commentRepository.save(comment);
        return savedComment.getCommentId();
    }
    
    // 특정 게시글 댓글 목록 조회
    @Transactional(readOnly = true)
    public List<CommentDto.Response> getCommentsByPost(Long postId){
        if(!postRepository.existsById(postId)){
            throw new IllegalArgumentException("존재하지 않는 게시물입니다.");
        }

        List<Comment> comments = commentRepository.findByPost_PostIdOrderByCreatedDateAsc(postId);

        return comments.stream()
                .map(CommentDto.Response::from)
                .collect(Collectors.toList());
    }

    // 댓글 수정
    @Transactional
    public CommentDto.Response updateComment(Long commentId, CommentDto.UpdateRequest request, Member loginMember){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
        if(!comment.getAuthorMember().getMemberId().equals(loginMember.getMemberId())){
            throw new IllegalArgumentException("본인이 작성한 댓글만 수정할 수 있습니다.");
        }
        comment.updateComment(request.getContent());

        return CommentDto.Response.from(comment);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, Member loginMember){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
        if(!comment.getAuthorMember().getMemberId().equals(loginMember.getMemberId())){
            throw new IllegalArgumentException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }
        commentRepository.delete(comment);
    }

}
