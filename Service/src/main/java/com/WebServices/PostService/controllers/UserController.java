package com.WebServices.PostService.controllers;

import com.WebServices.PostService.Exception400;
import com.WebServices.PostService.Exception406;
import com.WebServices.PostService.Exception409;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.WebServices.PostService.Exception404;
import com.WebServices.PostService.models.User;
import com.WebServices.PostService.repositories.UserRepository;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")

public class UserController {
    @Autowired
    UserRepository userRepository;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable(value = "id") Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new Exception404("(GET) api/users/id", "- no user found"));
    }

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user, HttpServletResponse response) {
        try {
            if (userRepository.existsById(user.getId())) {
                throw new Exception400();
            }

            if (user.getEmail() == null || user.getUsername() == null) {
                throw new Exception406();
            }

            if ((userRepository.findByEmail(user.getEmail())).size() != 0 ||
                    (userRepository.findByUsername(user.getUsername())).size() != 0) {
                throw new Exception409();
            }

            response.setStatus(201);
            User userNew = userRepository.save(user);
            response.addHeader("Location", "api/users/" + userNew.getId());
            return userNew;
        } catch (Exception400 ex) {
            throw new Exception400("(POST) api/users", "user exits with this id: " + user.getId());
        } catch (Exception406 ex) {
            throw new Exception406("(POST) api/users", "missing fields");
        } catch (Exception409 ex) {
            throw new Exception409("(POST) api/users", "user with these credentials already exits");
        }
    }

    @PutMapping("/users/{id}")
    public User updateUser(@PathVariable(value = "id") Long userId, @Valid @RequestBody User newUser, HttpServletResponse response) {
        User user = userRepository.findById(userId).orElseThrow(() -> new Exception404("(PUT) api/users/id", "- no such user"));
        try {
            if (newUser.getEmail() == null || newUser.getUsername() == null) {
                throw new Exception406();
            }

            if (userId != user.getId() &&
                    ((userRepository.findByEmail(newUser.getEmail())).size() != 0 ||
                            (userRepository.findByUsername(newUser.getUsername())).size() != 0)
                    ) {
                throw new Exception409();
            }

            user.setUsername(newUser.getUsername());
            user.setEmail(newUser.getEmail());

            response.setStatus(201);
            response.addHeader("Location", "api/users/" + userId);
            return userRepository.save(user);
        } catch (Exception406 ex) {
            throw new Exception406("(PUT) api/users", "missing fields");
        } catch (Exception409 ex) {
            throw new Exception409("(PUT) api/users", "user with these credentials already exits");
        }
    }

    @PatchMapping("/users/{id}")
    public User patchUser(@PathVariable(value = "id") Long userId, @Valid @RequestBody User newUser, HttpServletResponse response) {
        User user = userRepository.findById(userId).orElseThrow(() -> new Exception404("(PATCH) api/users/id", "- no such user"));
        try {
            if (newUser.getEmail() == null && newUser.getUsername() == null) {
                throw new Exception406();
            }

            if (newUser.getUsername() != null) {
                if (userId != user.getId() && (userRepository.findByUsername(newUser.getUsername())).size() != 0) {
                    throw new Exception409();
                }
                user.setUsername(newUser.getUsername());
            }

            if (newUser.getEmail() != null) {
                if (userId != user.getId() && (userRepository.findByEmail(newUser.getEmail())).size() != 0) {
                    throw new Exception409();
                }
                user.setEmail(newUser.getEmail());
            }

            response.setStatus(202);
            response.addHeader("Location", "api/users/" + userId);
            return userRepository.save(user);
        } catch (Exception409 ex) {
            throw new Exception409("(PATCH) api/users", "user with these credentials already exits");
        } catch (Exception406 ex) {
            throw new Exception406("(PATCH) api/users", "empty path request body");
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable(value = "id") Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new Exception404("(DELETE) api/users/id", "- no such user"));

        userRepository.delete(user);

        return ResponseEntity.noContent().build();
    }
}
