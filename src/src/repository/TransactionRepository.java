package com.splitwise.repository;

import com.splitwise.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Additional query methods can be defined here if needed
    // For example, to find transactions by payer or receiver, you could add:
    // List<Transaction> findByPayerId(Long payerId);
    // List<Transaction> findByReceiverId(Long receiverId);
}
