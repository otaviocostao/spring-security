package com.api.springsecurity.controller;

import com.api.springsecurity.controller.dto.CreatePostDTO;
import com.api.springsecurity.entities.Post;
import com.api.springsecurity.entities.Role;
import com.api.springsecurity.repositories.PostRepository;
import com.api.springsecurity.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
public class PostController {

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    public PostController(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/posts")
    public ResponseEntity<Void> createPost(@RequestBody CreatePostDTO dto, JwtAuthenticationToken token) {
        var user = userRepository.findById(UUID.fromString(token.getName()));

        var post = new Post();
        post.setUser(user.get());
        post.setTitle(dto.title());
        post.setContent(dto.content());

        postRepository.save(post);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, JwtAuthenticationToken token) {
        var user = userRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        var post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var userIsAdmin = user.getRoles()
                .stream()
                .anyMatch(role -> role.getRoleName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if (userIsAdmin || post.getUser().getUserId().equals(user.getUserId())) {
            postRepository.deleteById(postId);
        }else{
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok().build();
    }
}
