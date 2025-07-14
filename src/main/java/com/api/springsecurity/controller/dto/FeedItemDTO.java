package com.api.springsecurity.controller.dto;

public record FeedItemDTO(Long postId, String title, String content, String author) {
}
