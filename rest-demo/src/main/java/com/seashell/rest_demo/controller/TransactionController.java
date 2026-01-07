package com.seashell.rest_demo.controller;

import com.seashell.rest_demo.dto.ApiResponse;
import com.seashell.rest_demo.dto.TransactionRequest;
import com.seashell.rest_demo.dto.TransactionResponse;
import com.seashell.rest_demo.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {
    
    private final TransactionService transactionService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponse>> createTransaction(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody TransactionRequest request) {
        try {
            TransactionResponse response = transactionService.createTransaction(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("交易成功", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactions(
            @RequestHeader("X-User-Id") Long userId) {
        try {
            List<TransactionResponse> transactions = transactionService.getTransactions(userId);
            return ResponseEntity.ok(ApiResponse.success(transactions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("查詢交易記錄失敗: " + e.getMessage()));
        }
    }
}

