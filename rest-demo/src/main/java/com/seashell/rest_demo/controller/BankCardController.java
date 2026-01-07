package com.seashell.rest_demo.controller;

import com.seashell.rest_demo.dto.ApiResponse;
import com.seashell.rest_demo.entity.User;
import com.seashell.rest_demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/bank-cards")
@RequiredArgsConstructor
public class BankCardController {
    
    private final UserRepository userRepository;
    
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deleteBankCard(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        try {
            // 簡化：直接清除用戶的 cardNumber
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用戶不存在"));
            
            if (!userId.equals(id)) {
                throw new RuntimeException("無權限刪除此卡片");
            }
            
            user.setCardNumber(null);
            userRepository.save(user);
            return ResponseEntity.ok(ApiResponse.success("銀行卡刪除成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}

