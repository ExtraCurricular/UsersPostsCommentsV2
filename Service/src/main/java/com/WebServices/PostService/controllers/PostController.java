package com.WebServices.PostService.controllers;

import com.WebServices.PostService.*;
import com.WebServices.PostService.models.*;
import com.WebServices.PostService.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.WebServices.PostService.repositories.PostRepository;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<WeatherForecastDTO>> forecastResponse =
                restTemplate.exchange("http://userspostscommentsv2_WeatherService_1:5000/locations",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<WeatherForecastDTO>>() {
                        });

        if (forecastResponse.getStatusCode() == HttpStatus.OK) {
            List<WeatherForecastDTO> forecasts = forecastResponse.getBody();
            for (Post post : posts) {
                if (post.getLocation() != null) {
                    WeatherForecastDTO forecast = forecasts.stream().filter(
                            x -> x.getCity().equals(post.getLocation()) && fmt.format(x.getDate()).equals(fmt.format(post.getDate())))
                            .findFirst()
                            .orElse(null);
                    if (forecast != null) {
                        responsePosts.add(new PostDTO(post, forecast.getTemperature()));
                    }
                } else {
                    responsePosts.add(new PostDTO(post));
                }
            }
            return new ResponseEntity<>(responsePosts, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(posts, HttpStatus.OK);
        }
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<?> getPostById(@PathVariable(value = "id") Long postId) {

        Post post = postRepository.findById(postId).orElseThrow(() -> new Exception404("(GET) api/posts/id", "- no post found"));

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<WeatherForecastDTO>> forecastResponse =
                restTemplate.exchange("http://userspostscommentsv2_WeatherService_1:5000/locations",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<WeatherForecastDTO>>() {
                        });

        if (forecastResponse.getStatusCode() == HttpStatus.OK) {
            List<WeatherForecastDTO> forecasts = forecastResponse.getBody();
            if (post.getLocation() != null) {
                WeatherForecastDTO forecast = forecasts.stream().filter(
                        x -> x.getCity().equals(post.getLocation()) && fmt.format(x.getDate()).equals(fmt.format(post.getDate())))
                        .findFirst()
                        .orElse(null);
                if (forecast != null) {
                    return new ResponseEntity<>(new PostDTO(post, forecast.getTemperature()), HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @PostMapping("/posts")
    public Post createPost(@Valid @RequestBody Post post, HttpServletResponse response) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

            if (postRepository.existsById(post.getId())) {
                throw new Exception400();
            }

            if (post.getTitle() == null || post.getBody() == null || post.getUserId() == 0) {
                throw new Exception406();
            }
            post.setDate(new Date());

            if (post.getLocation() != null) {
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<List<WeatherForecastDTO>> forecastResponse =
                        restTemplate.exchange("http://userspostscommentsv2_WeatherService_1:5000/locations",
                                HttpMethod.GET, null, new ParameterizedTypeReference<List<WeatherForecastDTO>>() {
                                });
                if (forecastResponse.getStatusCode() == HttpStatus.OK) {
                    List<WeatherForecastDTO> forecasts = forecastResponse.getBody();
                    if (post.getLocation() != null) {
                        WeatherForecastDTO forecast = forecasts.stream().filter(
                                x -> x.getCity().equals(post.getLocation()) && fmt.format(x.getDate()).equals(fmt.format(post.getDate())))
                                .findFirst()
                                .orElse(null);
                        if (forecast == null) {
                            Random rand = new Random();
                            HttpHeaders headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_JSON);
                            PostWeatherDTO postWeatherDTO = new PostWeatherDTO(
                                    post.getLocation(),
                                    fmt.format(new Date()),
                                    (float) rand.nextInt(30) - 20
                            );

                            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                            String json = ow.writeValueAsString(postWeatherDTO);
                            HttpEntity<String> request = new HttpEntity<>(json);
                            ResponseEntity<String> postResponse = restTemplate.exchange("http://userspostscommentsv2_WeatherService_1:5000/locations",
                                    HttpMethod.POST, request, String.class);
                            if (postResponse.getStatusCode() != HttpStatus.CREATED){
                                throw new Exception503("(POST) api/posts", "the weather forecast service responded with an error code");
                            }
                        }
                    }
                }
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
        } catch (JsonProcessingException ex) {
            throw new Exception400("(POST) api/users", "JSON formatting failed with id " + post.getId());
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