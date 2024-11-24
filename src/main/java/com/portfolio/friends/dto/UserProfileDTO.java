package com.portfolio.friends.dto;

import org.springframework.data.domain.Page;

public record UserProfileDTO(String username, Page<String> friendships) {
}
