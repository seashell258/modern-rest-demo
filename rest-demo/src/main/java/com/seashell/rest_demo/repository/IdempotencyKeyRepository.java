package com.seashell.rest_demo.repository;

import com.seashell.rest_demo.entity.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, Long> {
    
    /**
     * 根據冪等性 key 查找記錄
     */
    Optional<IdempotencyKey> findByIdempotencyKey(String idempotencyKey);
    
    /**
     * 根據用戶 ID、端點和請求 hash 查找記錄
     */
    Optional<IdempotencyKey> findByUserIdAndEndpointAndRequestHash(
            Long userId, String endpoint, String requestHash);
    
    /**
     * 檢查冪等性 key 是否存在且未過期
     */
    @Query("SELECT ik FROM IdempotencyKey ik WHERE ik.idempotencyKey = :key AND ik.expiresAt > :now")
    Optional<IdempotencyKey> findValidByIdempotencyKey(
            @Param("key") String idempotencyKey, 
            @Param("now") LocalDateTime now);
    
    /**
     * 刪除過期的冪等性記錄
     */
    @Modifying
    @Query("DELETE FROM IdempotencyKey ik WHERE ik.expiresAt < :now")
    void deleteExpiredKeys(@Param("now") LocalDateTime now);
}

