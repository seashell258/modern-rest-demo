package com.seashell.rest_demo.service;

import com.seashell.rest_demo.dto.UserProfileRequest;
import com.seashell.rest_demo.dto.UserProfileResponse;
import com.seashell.rest_demo.entity.User;
import com.seashell.rest_demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    @Transactional
    public UserProfileResponse updateUserProfile(Long userId, UserProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));
        
        // 部分更新（PATCH）
        if (request.fullName() != null) {
            user.setFullName(request.fullName());
        }
        if (request.email() != null) {
            // 檢查 email 是否已被其他用戶使用
            if (userRepository.existsByEmail(request.email()) && 
                !user.getEmail().equals(request.email())) {
                throw new RuntimeException("該電子郵件已被使用");
            }
            user.setEmail(request.email());
        }
        if (request.phoneNumber() != null) {
            user.setPhoneNumber(request.phoneNumber());
        }
        if (request.cardNumber() != null) {
            user.setCardNumber(request.cardNumber());
        }
        
        user = userRepository.save(user);
        return UserProfileResponse.fromEntity(user);
    }
    
    @Transactional
    public UserProfileResponse replaceUserProfile(Long userId, UserProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));
        
        // 完整更新（PUT）- 完整替換所有欄位，不檢查 null
        // PUT 必須傳所有欄位，null 表示要清空該欄位
        user.setFullName(request.fullName());
        
        // 檢查 email 是否已被其他用戶使用
        if (request.email() != null && 
            userRepository.existsByEmail(request.email()) && 
            !user.getEmail().equals(request.email())) {
            throw new RuntimeException("該電子郵件已被使用");
        }
        user.setEmail(request.email());
        
        // PUT 直接設定，允許 null（清空欄位）
        user.setPhoneNumber(request.phoneNumber());
        user.setCardNumber(request.cardNumber());
        
        user = userRepository.save(user);
        return UserProfileResponse.fromEntity(user);
    }
}

