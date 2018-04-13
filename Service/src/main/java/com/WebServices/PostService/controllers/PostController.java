package com.WebServices.PostService.controllers;

import com.WebServices.PostService.*;
import com.WebServices.PostService.models.*;
import com.WebServices.PostService.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.WebServices.PostService.repositories.PostRepository;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")

public class PostController {
    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/posts")
    public ResponseEntity<?> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        List<PostDTO> responsePosts = new ArrayList<>();

        RestTemplate restTemplate = new RestTemplate();

        for (Post post : posts) {
            if (post.getWeatherId() != 0) {
                ResponseEntity<Location> forecastResponse =
                        restTemplate.exchange("http://userspostscommentsv2_WeatherService_1:5000/locations/" + post.getWeatherId(),
                                HttpMethod.GET, null, new ParameterizedTypeReference<Location>() {
                                });
                if (forecastResponse.getStatusCode() == HttpStatus.OK) {
                    PostDTO postNew = new PostDTO();
                    postNew.setUserId(post.getUserId());
                    postNew.setBody(post.getBody());
                    postNew.setTitle(post.getTitle());
                    postNew.setId(post.getId());
                    postNew.setLocation(forecastResponse.getBody());
                    responsePosts.add(postNew);
                } else {
                    throw new Exception503("(GET) api/posts/id", "the weather service did not respond");
                }
            } else {
                PostDTO postNew = new PostDTO();
                postNew.setUserId(post.getUserId());
                postNew.setBody(post.getBody());
                postNew.setTitle(post.getTitle());
                postNew.setId(post.getId());
                responsePosts.add(postNew);
            }
        }
        return new ResponseEntity<>(responsePosts, HttpStatus.OK);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<?> getPostById(@PathVariable(value = "id") Long postId) {

        Post post = postRepository.findById(postId).orElseThrow(() -> new Exception404("(GET) api/posts/id", "- no post found"));

        if (post.getWeatherId() != 0) {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Location> forecastResponse =
                    restTemplate.exchange("http://userspostscommentsv2_WeatherService_1:5000/locations/" + post.getWeatherId(),
                            HttpMethod.GET, null, new ParameterizedTypeReference<Location>() {
                            });
            if (forecastResponse.getStatusCode() == HttpStatus.OK) {
                PostDTO postNew = new PostDTO();
                postNew.setUserId(post.getUserId());
                postNew.setBody(post.getBody());
                postNew.setTitle(post.getTitle());
                postNew.setId(post.getId());
                postNew.setLocation(forecastResponse.getBody());
                return new ResponseEntity<>(postNew, HttpStatus.OK);
            } else {
                throw new Exception503("(GET) api/posts/id", "the weather service did not respond");
            }
        }
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @PostMapping("/posts")
    public Post createPost(@Valid @RequestBody PostDTO post, HttpServletResponse response) {
        try {
            if (postRepository.existsById(post.getId())) {
                throw new Exception400("post exits with this id: " + post.getId());
            }

            if (post.getTitle() == null || post.getBody() == null || post.getUserId() == 0) {
                throw new Exception406();
            }

            Post postNew = new Post();


            if (post.getLocation() != null) {
                if (post.getLocation().getCity() == null || post.getLocation().getDate() == null) {
                    throw new Exception400("invalid location request!");
                }
                RestTemplate restTemplate = new RestTemplate();
                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                String json = ow.writeValueAsString(post.getLocation());
                HttpEntity<String> request = new HttpEntity<>(json);
                ResponseEntity<String> postResponse = restTemplate.exchange("http://userspostscommentsv2_WeatherService_1:5000/locations",
                        HttpMethod.POST, request, String.class);
                if (postResponse.getStatusCode() == HttpStatus.CREATED) {
                    System.out.println(postResponse.getBody());
                    HttpHeaders headers = postResponse.getHeaders();
                    String location = headers.getLocation().toString();
                    int index = location.lastIndexOf('/');
                    int id = Integer.parseInt(location.substring(index + 1, location.length()));
                    postNew.setWeatherId(id);
                } else {
                    throw new Exception503("(POST) api/posts", "the weather forecast service responded with an error code");
                }
            }

            userRepository.findById(post.getUserId()).orElseThrow(() -> new Exception409());

            postNew.setBody(post.getBody());
            postNew.setTitle(post.getTitle());
            postNew.setUserId(post.getUserId());

            System.out.println("55555555555555555555555555555555555555555555555555555555555555555555555555555555555555");

            response.setStatus(201);
            Post postNewest = postRepository.save(postNew);
            response.addHeader("Location", "api/posts/" + postNewest.getId());
            return postNewest;
        } catch (Exception400 ex) {
            throw new Exception400("(POST) api/users", ex.getReason());
        } catch (Exception406 ex) {
            throw new Exception406("(POST) api/users", "missing fields");
        } catch (Exception409 ex) {
            throw new Exception409("(POST) api/users", "no such user with id: " + post.getUserId());
        } catch (JsonProcessingException ex) {
            throw new Exception400("(POST) api/users", "JSON formatting failed with id " + post.getId());
        } catch (Exception ex) {
            throw new Exception503("(POST) api/users", "the weather forecast service is unavailable");
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
    public Post patchPost(@PathVariable(value = "id") Long postId, @Valid @RequestBody PostDTO newPost, HttpServletResponse response) {
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

        if (post.getWeatherId() != 0) {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> deleteResponse =
                    restTemplate.exchange("http://userspostscommentsv2_WeatherService_1:5000/locations/" + post.getWeatherId(),
                            HttpMethod.DELETE, null, new ParameterizedTypeReference<String>() {
                            });
            if(deleteResponse.getStatusCode() != HttpStatus.OK){
                throw new Exception503("(DELETE) api/posts", "the weather forecast service responded with an error code");
            }
        }

        postRepository.delete(post);

        return ResponseEntity.noContent().build();
    }
}