package com.WebServices.PostService.controllers;

import com.WebServices.PostService.Exception400;
import com.WebServices.PostService.Exception406;
import com.WebServices.PostService.Exception409;
import com.WebServices.PostService.models.Post;
import com.WebServices.PostService.models.User;
import com.WebServices.PostService.repositories.PostRepository;
import com.WebServices.PostService.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.WebServices.PostService.Exception404;
import com.WebServices.PostService.models.Comment;
import com.WebServices.PostService.repositories.CommentRepository;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")

public class CommentController {
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @GetMapping("/comments")
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    @GetMapping("/comments/{id}")
    public Comment getCommentById(@PathVariable(value = "id") Long commentsId) {
        return commentRepository.findById(commentsId).orElseThrow(() -> new Exception404("(GET) api/comments/id", "- no comment found"));
    }

    @PostMapping("/comments")
    public Comment createComment(@Valid @RequestBody Comment comment, HttpServletResponse response) {
        try {
            if (commentRepository.existsById(comment.getId())) {
                throw new Exception400();
            }

            if (comment.getBody() == null || comment.getUserId() == 0 || comment.getPostId() == 0) {
                throw new Exception406();
            }

            userRepository.findById(comment.getUserId()).orElseThrow(() -> new Exception409());
            postRepository.findById(comment.getPostId()).orElseThrow(() -> new Exception409());

            response.setStatus(201);
            Comment commentNew = commentRepository.save(comment);
            response.addHeader("Location", "api/comments/" + commentNew.getId());
            return commentNew;
        } catch (Exception400 ex) {
            throw new Exception400("(POST) api/comments", "comment exits with this id: " + comment.getId());
        } catch (Exception406 ex) {
            throw new Exception406("(POST) api/comments", "missing fields");
        } catch (Exception409 ex) {
            throw new Exception409("(POST) api/comments", "no such user or post was found");
        }
    }

    @PutMapping("/comments/{id}")
    public Comment updateComment(@PathVariable(value = "id") Long commentId, @Valid @RequestBody Comment newComment, HttpServletResponse response) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new Exception404("(PUT) api/comments/id", "- no such comment"));
        try {
            if (newComment.getUserId() == 0 || newComment.getBody() == null || newComment.getPostId() == 0) {
                throw new Exception406();
            }

            userRepository.findById(comment.getUserId()).orElseThrow(() -> new Exception409());
            postRepository.findById(comment.getUserId()).orElseThrow(() -> new Exception409());

            comment.setPostId(newComment.getPostId());
            comment.setBody(newComment.getBody());
            comment.setUserId(newComment.getUserId());

            response.setStatus(201);
            response.addHeader("Location", "api/comments/" + commentId);
            return commentRepository.save(comment);
        } catch (Exception406 ex) {
            throw new Exception406("(PUT) api/comments", "missing fields");
        } catch (Exception409 ex) {
            throw new Exception409("(PUT) api/comments", "no such user or post was found");
        }
    }

    @PatchMapping("/comments/{id}")
    public Comment patchComment(@PathVariable(value = "id") Long commentId, @Valid @RequestBody Comment newComment, HttpServletResponse response) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new Exception404("(PATCH) api/comments/id", "- no such comment"));
        try {
            userRepository.findById(comment.getUserId()).orElseThrow(() -> new Exception409());
            postRepository.findById(comment.getPostId()).orElseThrow(() -> new Exception409());

            if (newComment.getPostId() == 0 && newComment.getBody() == null && newComment.getUserId() == 0) {
                throw new Exception406();
            }

            if (newComment.getPostId() != 0) {
                comment.setPostId(newComment.getPostId());
            }

            if (newComment.getBody() != null) {
                comment.setBody(newComment.getBody());
            }

            if (newComment.getUserId() != 0) {
                comment.setUserId(newComment.getUserId());
            }

            response.setStatus(202);
            response.addHeader("Location", "api/comments/" + commentId);
            return commentRepository.save(comment);
        } catch (Exception406 ex) {
            throw new Exception406("(PATCH) api/comments", "empty path request body");
        } catch (Exception409 ex) {
            throw new Exception409("(PUT) api/comments", "no such user or post was found");
        }
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable(value = "id") Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new Exception404("(DELETE) api/comments/id", ""));

        commentRepository.delete(comment);

        return ResponseEntity.noContent().build();
    }
}