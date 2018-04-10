package com.WebServices.PostService.controllers;

import com.WebServices.PostService.Exception400;
import com.WebServices.PostService.Exception406;
import com.WebServices.PostService.Exception409;
import com.WebServices.PostService.models.User;
import com.WebServices.PostService.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.WebServices.PostService.Exception404;
import com.WebServices.PostService.models.Post;
import com.WebServices.PostService.repositories.PostRepository;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")

public class PostController {
    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/posts")
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @GetMapping("/posts/{id}")
    public Post getPostById(@PathVariable(value = "id") Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new Exception404("(GET) api/posts/id", "- no post found"));
    }

    @PostMapping("/posts")
    public Post createPost(@Valid @RequestBody Post post, HttpServletResponse response) {
        try {
            if (postRepository.existsById(post.getId())) {
                throw new Exception400();
            }

            if (post.getTitle() == null || post.getBody() == null || post.getUserId() == 0) {
                throw new Exception406();
            }

            userRepository.findById(post.getUserId()).orElseThrow(() -> new Exception409());

            response.setStatus(201);
            Post postNew = postRepository.save(post);
            response.addHeader("Location", "api/posts/" + postNew.getId());
            return postNew;
        } catch (Exception400 ex) {
            throw new Exception400("(POST) api/users", "post exits with this id: " + post.getId());
        } catch (Exception406 ex) {
            throw new Exception406("(POST) api/users", "missing fields");
        } catch (Exception409 ex) {
            throw new Exception409("(POST) api/users", "no such user with id: " + post.getUserId());
        }
    }

    @PutMapping("/posts/{id}")
    public Post updatePost(@PathVariable(value = "id") Long postId, @Valid @RequestBody Post newPost, HttpServletResponse response) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new Exception404("(PUT) api/posts/id", "- no such post"));
        try {
            if (newPost.getUserId() == 0 || newPost.getBody() == null || newPost.getTitle() == null) {
                throw new Exception406();
            }

            userRepository.findById(post.getUserId()).orElseThrow(() -> new Exception409());

            post.setTitle(newPost.getTitle());
            post.setBody(newPost.getBody());
            post.setUserId(newPost.getUserId());

            response.setStatus(201);
            response.addHeader("Location", "api/posts/" + postId);
            return postRepository.save(post);
        } catch (Exception406 ex) {
            throw new Exception406("(PUT) api/posts", "missing fields");
        } catch (Exception409 ex) {
            throw new Exception409("(PUT) api/posts", "user with id: " + post.getUserId() + " does not exist");
        }
    }

    @PatchMapping("/posts/{id}")
    public Post patchPost(@PathVariable(value = "id") Long postId, @Valid @RequestBody Post newPost, HttpServletResponse response) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new Exception404("(PATCH) api/posts/id", "- no such post"));
        try {
            userRepository.findById(post.getUserId()).orElseThrow(() -> new Exception409());

            if (newPost.getTitle() == null && newPost.getBody() == null && newPost.getUserId() == 0) {
                throw new Exception406();
            }

            if (newPost.getTitle() != null) {
                post.setTitle(newPost.getTitle());
            }

            if (newPost.getBody() != null) {
                post.setBody(newPost.getBody());
            }

            if (newPost.getUserId() != 0) {
                post.setUserId(newPost.getUserId());
            }

            response.setStatus(202);
            response.addHeader("Location", "api/posts/" + postId);
            return postRepository.save(post);
        } catch (Exception406 ex) {
            throw new Exception406("(PATCH) api/posts", "empty patch request body");
        } catch (Exception409 ex) {
            throw new Exception409("(PUT) api/posts", "user with id: " + post.getUserId() + " does not exist");
        }
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<?> deletePost(@PathVariable(value = "id") Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new Exception404("(DELETE) api/posts/id", ""));

        postRepository.delete(post);

        return ResponseEntity.noContent().build();
    }
}