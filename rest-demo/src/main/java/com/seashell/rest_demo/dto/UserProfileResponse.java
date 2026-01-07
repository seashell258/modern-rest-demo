package com.seashell.rest_demo.dto;

import com.seashell.rest_demo.entity.User;

import java.time.LocalDateTime;

// Java Record - 響應物件
public record UserProfileResponse(
        Long id,
        String username,
        String email,
        String fullName,
        String phoneNumber,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UserProfileResponse fromEntity(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
