package com.seashell.rest_demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "idempotency_keys", indexes = {
    @Index(name = "idx_idempotency_key", columnList = "idempotency_key", unique = true),
    @Index(name = "idx_user_endpoint", columnList = "user_id, endpoint"),
    @Index(name = "idx_expires_at", columnList = "expires_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdempotencyKey {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "idempotency_key", nullable = false, unique = true, length = 255)
    private String idempotencyKey;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "endpoint", nullable = false, length = 255)
    private String endpoint;  // 例如: '/v1/transactions'
    
    @Column(name = "request_hash", length = 64)
    private String requestHash;  // 請求內容的 SHA-256 hash
    
    @Column(name = "response", columnDefinition = "TEXT")
    private String response;  // 儲存 JSON 格式的回應內容
    
    @Column(name = "status_code")
    private Integer statusCode;  // HTTP 狀態碼
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        // 預設過期時間為 24 小時後
        if (expiresAt == null) {
            expiresAt = LocalDateTime.now().plusHours(24);
        }
    }
}

