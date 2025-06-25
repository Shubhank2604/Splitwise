package com.splitwise.service;

import com.splitwise.dto.UserBalanceDTO;
import com.splitwise.dto.UserBalanceResponse;
import com.splitwise.entity.Expense;
import com.splitwise.entity.Split;
import com.splitwise.entity.User;
import com.splitwise.repository.ExpenseRepository;
import com.splitwise.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserBalanceService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

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

        // 3. Convert to list of DTOs
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
}
