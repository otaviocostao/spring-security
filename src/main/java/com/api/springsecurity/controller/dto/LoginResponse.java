package com.api.springsecurity.controller.dto;

public record LoginResponse(String accessToken, Long expiresIn) {
}
