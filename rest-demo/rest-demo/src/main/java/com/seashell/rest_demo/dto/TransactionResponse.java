package com.seashell.rest_demo.dto;

import com.seashell.rest_demo.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Java Record - 不可變的資料傳輸物件
public record TransactionResponse(
        Long id,
        Long fromUserId,
        String fromUsername,
        Long toUserId,
        String toUsername,
        BigDecimal amount,
        String currency,
        String transactionType,
        String status,
        String description,
        LocalDateTime createdAt
) {
    public static TransactionResponse fromEntity(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getFromUser().getId(),
                transaction.getFromUser().getUsername(),
                transaction.getToUser().getId(),
                transaction.getToUser().getUsername(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getTransactionType().name(),
                transaction.getStatus().name(),
                transaction.getDescription(),
                transaction.getCreatedAt()
        );
    }
}
