package com.seashell.rest_demo.dto;

import jakarta.validation.constraints.Email;

// Java Record - 用於 PATCH/PUT 請求
// 注意：username 和 password 不在這裡，因為這些通常有專門的端點處理
public record UserProfileRequest(
        String fullName,
        
        @Email(message = "電子郵件格式不正確")
        String email,
        
        String phoneNumber,
        
        String cardNumber  // 銀行卡號
) {
}
