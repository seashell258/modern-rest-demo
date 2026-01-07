package com.seashell.rest_demo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

// Java Record - 自動生成 getter, equals, hashCode, toString
public record TransactionRequest(
        @NotNull(message = "收款用戶ID不能為空")
        Long toUserId,
        
        @NotNull(message = "金額不能為空")
        @DecimalMin(value = "0.01", message = "金額必須大於0")
        BigDecimal amount,
        
        @NotBlank(message = "交易類型不能為空")
        String transactionType,  // TRANSFER 或 PAYMENT
        
        String description,
        
        String currency
) {
    // 提供預設值
    public TransactionRequest {
        if (currency == null) {
            currency = "TWD";
        }
    }
}
