package com.splitwise.service;

import com.splitwise.entity.Transaction;
import com.splitwise.entity.User;
import com.splitwise.repository.TransactionRepository;
import com.splitwise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserBalanceService userBalanceService;


    public void settleUp(Long payerId, Long receiverId, Double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }

        User payer = userRepository.findById(payerId)
                .orElseThrow(() -> new RuntimeException("Payer not found"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        // Save transaction
        Transaction txn = new Transaction();
        txn.setPayer(payer);
        txn.setReceiver(receiver);
        txn.setAmount(amount);
        txn.setDate(new Date());

        transactionRepository.save(txn);
        userBalanceService.settleDebt(
                payerId,
                receiverId,
                null,         // no groupId for personal transactions
                amount
        );

    }
}
