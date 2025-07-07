package com.splitwise.service;

import com.splitwise.dto.UserBalanceDTO;
import com.splitwise.dto.UserBalanceResponse;
import com.splitwise.entity.*;
import com.splitwise.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserBalanceService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserBalanceRepository userBalanceRepository;

    @Autowired
    private GroupRepository groupRepository;

    public UserBalanceResponse getUserBalance(Long userId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<Long, Double> balances = new HashMap<>(); // userId -> net balance
        Map<Long, String> userIdToName = new HashMap<>();

        // 1. Expenses paid by current user
        for (Expense expense : currentUser.getExpensesPaid()) {
            for (Split split : expense.getSplits()) {
                if (!split.getUser().getId().equals(userId)) {
                    Long otherUserId = split.getUser().getId();
                    balances.put(otherUserId, balances.getOrDefault(otherUserId, 0.0) + split.getAmount());
                    userIdToName.put(otherUserId, split.getUser().getUsername());
                }
            }
        }

        // 2. Expenses where current user owes others
        for (Split split : currentUser.getSplits()) {
            Expense expense = split.getExpense();
            Long paidByUserId = expense.getPaidBy().getId();
            if (!paidByUserId.equals(userId)) {
                balances.put(paidByUserId, balances.getOrDefault(paidByUserId, 0.0) - split.getAmount());
                userIdToName.put(paidByUserId, expense.getPaidBy().getUsername());
            }
        }

        // 3. Adjust balances based on settle-up transactions
        List<Transaction> allTransactions = transactionRepository.findAll();

        for (Transaction txn : allTransactions) {
            Long payerId = txn.getPayer().getId();
            Long receiverId = txn.getReceiver().getId();
            double amount = txn.getAmount();

            if (payerId.equals(userId)) {
                // current user paid someone → reduce what they owe
                balances.put(receiverId, balances.getOrDefault(receiverId, 0.0) + amount);
                userIdToName.put(receiverId, txn.getReceiver().getUsername());
            }

            if (receiverId.equals(userId)) {
                // someone paid current user → reduce what they owe you
                balances.put(payerId, balances.getOrDefault(payerId, 0.0) - amount);
                userIdToName.put(payerId, txn.getPayer().getUsername());
            }
        }

        // 4. Convert to list of DTOs
        List<UserBalanceDTO> balanceDTOs = new ArrayList<>();
        double net = 0.0;

        for (Map.Entry<Long, Double> entry : balances.entrySet()) {
            double amount = entry.getValue();
            if (Math.abs(amount) >= 0.01) { // ignore zero-ish balances
                net += amount;
                System.out.println("userId: " + entry.getKey());
                System.out.println("name: " + userIdToName.get(entry.getKey()));
                System.out.println("amount: " + entry.getValue());
                UserBalanceDTO userBalanceDTO = new UserBalanceDTO(entry.getKey(), userIdToName.get(entry.getKey()), amount);
                balanceDTOs.add(userBalanceDTO);
            }
        }

        return new UserBalanceResponse(balanceDTOs, net);
    }

    public void addDebt(Long fromUserId, Long toUserId, Long groupId, Double amount) {
        UserBalance existing = userBalanceRepository
                .findByFromUserIdAndToUserIdAndGroupId(fromUserId, toUserId, groupId);

        if (existing != null) {
            existing.setAmount(existing.getAmount() + amount);
            userBalanceRepository.save(existing);
        } else {
            UserBalance newBalance = new UserBalance();
            newBalance.setFromUser(userRepository.getReferenceById(fromUserId));
            newBalance.setToUser(userRepository.getReferenceById(toUserId));
            newBalance.setAmount(amount);

            if (groupId != null) {
                newBalance.setGroup(groupRepository.getReferenceById(groupId));
            }

            userBalanceRepository.save(newBalance);
        }
    }

    public void settleDebt(Long fromUserId, Long toUserId, Long groupId, Double amount) {
        UserBalance existing = userBalanceRepository
                .findByFromUserIdAndToUserIdAndGroupId(fromUserId, toUserId, groupId);

        if (existing != null) {
            existing.setAmount(existing.getAmount() - amount);

            if (Math.abs(existing.getAmount()) < 0.01) {
                userBalanceRepository.delete(existing);
            } else {
                userBalanceRepository.save(existing);
            }
        }
    }
}
