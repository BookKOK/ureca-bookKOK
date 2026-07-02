package com.bookkok.board.controller;

import com.bookkok.board.dto.CommentDto;
import com.bookkok.board.entity.Comment;
import com.bookkok.board.service.CommentService;
import com.bookkok.member.entity.Member;
import com.bookkok.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;
    private final MemberRepository memberRepository;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Long> createComment(
            @PathVariable("postId") Long postId,
            @RequestBody CommentDto.CreateRequest request,
            @AuthenticationPrincipal User loginUser){

        Member loginMember = memberRepository.findById(loginUser.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("회원찾을수없음"));
        Long commentId = commentService.createComment(postId, request, loginMember);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentId);
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentDto.Response>> getComments(@PathVariable("postId") Long postId){
        List<CommentDto.Response> responses = commentService.getCommentsByPost(postId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentDto.Response> updateComment(
            @PathVariable("commentId") Long commentId,
            @RequestBody CommentDto.UpdateRequest request,
            @AuthenticationPrincipal User loginUser){
        Member loginMember = memberRepository.findById(loginUser.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("회원찾을수없음"));
        CommentDto.Response response = commentService.updateComment(commentId, request, loginMember);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId,
                                              @AuthenticationPrincipal User loginUser){

        Member loginMember = memberRepository.findById(loginUser.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("회원찾을수없음"));
        commentService.deleteComment(commentId, loginMember);
        return ResponseEntity.ok().build();
    }

}
