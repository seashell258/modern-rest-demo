package com.seashell.rest_demo.repository;

import com.seashell.rest_demo.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByFromUserId(Long userId);
    List<Transaction> findByToUserId(Long userId);
    
    @Query("SELECT t FROM Transaction t WHERE t.fromUser.id = :userId OR t.toUser.id = :userId ORDER BY t.createdAt DESC")
    List<Transaction> findAllByUserId(@Param("userId") Long userId);
}

