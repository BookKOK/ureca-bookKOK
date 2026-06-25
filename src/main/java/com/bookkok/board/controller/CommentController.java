package com.bookkok.board.controller;

import com.bookkok.board.dto.CommentDto;
import com.bookkok.board.entity.Comment;
import com.bookkok.board.service.CommentService;
import com.bookkok.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Long> createComment(
            @PathVariable Long postId,
            @RequestBody CommentDto.CreateRequest request,
            @AuthenticationPrincipal User loginUser){

        Long commentId = commentService.createComment(request, loginUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentId);
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentDto.Response>> getComments(@PathVariable Long postId){
        List<CommentDto.Response> responses = commentService.getCommentsByPost(postId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentDto.Response> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentDto.UpdateRequest request,
            @AuthenticationPrincipal User loginUser){
        CommentDto.Response response = commentService.updateComment(commentId, request, loginUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId,
                                              @AuthenticationPrincipal User loginUser){

        commentService.deleteComment(commentId, loginUser);
        return ResponseEntity.ok().build();
    }

}
