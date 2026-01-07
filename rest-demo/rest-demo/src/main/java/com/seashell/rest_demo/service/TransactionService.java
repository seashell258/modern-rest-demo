package com.seashell.rest_demo.service;

import com.seashell.rest_demo.dto.TransactionRequest;
import com.seashell.rest_demo.dto.TransactionResponse;
import com.seashell.rest_demo.entity.Transaction;
import com.seashell.rest_demo.entity.User;
import com.seashell.rest_demo.entity.Wallet;
import com.seashell.rest_demo.repository.TransactionRepository;
import com.seashell.rest_demo.repository.UserRepository;
import com.seashell.rest_demo.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    
    @Transactional
    public TransactionResponse createTransaction(Long fromUserId, TransactionRequest request) {
        // 驗證用戶存在
        User fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new RuntimeException("發起用戶不存在"));
        
        User toUser = userRepository.findById(request.toUserId())
                .orElseThrow(() -> new RuntimeException("收款用戶不存在"));
        
        if (fromUserId.equals(request.toUserId())) {
            throw new RuntimeException("不能向自己轉帳");
        }
        
        // 獲取錢包
        Wallet fromWallet = walletRepository.findByUserId(fromUserId)
                .orElseThrow(() -> new RuntimeException("發起用戶錢包不存在"));
        
        Wallet toWallet = walletRepository.findByUserId(request.toUserId())
                .orElseThrow(() -> new RuntimeException("收款用戶錢包不存在"));
        
        // 驗證餘額
        if (fromWallet.getBalance().compareTo(request.amount()) < 0) {
            throw new RuntimeException("餘額不足");
        }
        
        // 建立交易記錄
        Transaction transaction = new Transaction();
        transaction.setFromUser(fromUser);
        transaction.setToUser(toUser);
        transaction.setAmount(request.amount());
        transaction.setCurrency(request.currency());
        transaction.setDescription(request.description());
        
        // 設定交易類型
        try {
            transaction.setTransactionType(Transaction.TransactionType.valueOf(request.transactionType()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("無效的交易類型");
        }
        
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        
        // 執行轉帳
        try {
            // 扣除發起用戶餘額
            fromWallet.setBalance(fromWallet.getBalance().subtract(request.getAmount()));
            walletRepository.save(fromWallet);
            
            // 增加收款用戶餘額
            toWallet.setBalance(toWallet.getBalance().add(request.getAmount()));
            walletRepository.save(toWallet);
            
            // 更新交易狀態為完成
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
            transaction = transactionRepository.save(transaction);
            
            return TransactionResponse.fromEntity(transaction);
        } catch (Exception e) {
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new RuntimeException("交易失敗: " + e.getMessage());
        }
    }
    
    public List<TransactionResponse> getTransactions(Long userId) {
        List<Transaction> transactions = transactionRepository.findAllByUserId(userId);
        return transactions.stream()
                .map(TransactionResponse::fromEntity)
                .collect(Collectors.toList());
    }
}

