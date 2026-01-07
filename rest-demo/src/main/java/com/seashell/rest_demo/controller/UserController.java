package com.seashell.rest_demo.controller;

import com.seashell.rest_demo.dto.ApiResponse;
import com.seashell.rest_demo.dto.UserProfileRequest;
import com.seashell.rest_demo.dto.UserProfileResponse;
import com.seashell.rest_demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateUserProfile(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UserProfileRequest request) {
        try {
            UserProfileResponse response = userService.updateUserProfile(userId, request);
            return ResponseEntity.ok(ApiResponse.success("個人資料更新成功", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> replaceUserProfile(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UserProfileRequest request) {
        try {
            UserProfileResponse response = userService.replaceUserProfile(userId, request);
            return ResponseEntity.ok(ApiResponse.success("個人資料更新成功", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}

