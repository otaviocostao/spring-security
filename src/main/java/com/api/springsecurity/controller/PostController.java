package com.api.springsecurity.controller;

import com.api.springsecurity.controller.dto.CreatePostDTO;
import com.api.springsecurity.controller.dto.FeedDTO;
import com.api.springsecurity.controller.dto.FeedItemDTO;
import com.api.springsecurity.entities.Post;
import com.api.springsecurity.entities.Role;
import com.api.springsecurity.repositories.PostRepository;
import com.api.springsecurity.repositories.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@RestController
public class PostController {

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    public PostController(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/feed")
    public ResponseEntity<FeedDTO> getFeed(@RequestParam(value = "page", defaultValue = "0") int page,
                                           @RequestParam(value = "size", defaultValue = "10") int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        var postsPage = postRepository.findAll(pageable)
                .map(post -> new FeedItemDTO(
                        post.getPostId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getUser().getUsername()));

        var feedDto = new FeedDTO(
                postsPage.getContent(),
                page,
                size,
                postsPage.getTotalPages(),
                postsPage.getTotalElements());

        return ResponseEntity.ok(feedDto);
    }

    @PostMapping("/posts")
    public ResponseEntity<Void> createPost(@RequestBody CreatePostDTO dto, JwtAuthenticationToken token) {

        var user = userRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User from token not found"));

        var post = new Post();
        post.setUser(user);
        post.setTitle(dto.title());
        post.setContent(dto.content());

        var savedPost = postRepository.save(post);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{postId}")
                .buildAndExpand(savedPost.getPostId())
                .toUri();


        return ResponseEntity.created(location).build();
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
