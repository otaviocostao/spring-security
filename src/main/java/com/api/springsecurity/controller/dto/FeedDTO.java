package com.api.springsecurity.controller.dto;

import java.util.List;

public record FeedDTO(List<FeedItemDTO> feedItems, int page, int size, int totalPages, long totalElements) {

}
