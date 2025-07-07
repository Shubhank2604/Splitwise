package com.splitwise.service;

import com.splitwise.dto.*;
import com.splitwise.entity.*;
import com.splitwise.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class ExpenseService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private SplitRepository splitRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private UserBalanceService userBalanceService;

    public Expense createExpense(CreateExpenseRequest request) {
        User paidBy = userRepository.findById(request.getPaidByUserId())
                .orElseThrow(() -> new RuntimeException("Payer not found"));

        // Group membership validation
        if (request.getGroupId() != null) {
            List<Long> groupMemberIds = groupMemberRepository.findByGroupId(request.getGroupId())
                    .stream()
                    .map(GroupMember::getUserId)
                    .toList();

            for (Long userId : request.getInvolvedUserIds()) {
                if (!groupMemberIds.contains(userId)) {
                    throw new IllegalArgumentException("User " + userId + " is not a member of group " + request.getGroupId());
                }
            }

            if (!groupMemberIds.contains(paidBy.getId())) {
                throw new IllegalArgumentException("Payer " + paidBy.getId() + " is not a member of group " + request.getGroupId());
            }
        }

        Expense expense = new Expense();
        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        expense.setDate(new Date());
        expense.setGroupId(request.getGroupId());
        expense.setPaidBy(paidBy);

        List<Split> splits = new ArrayList<>();
        for (int i = 0; i < request.getInvolvedUserIds().size(); i++) {
            Long userId = request.getInvolvedUserIds().get(i);
            Double splitAmount = request.getSplitAmounts().get(i);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));
            Split split = new Split();
            split.setAmount(splitAmount);
            split.setUser(user);
            split.setExpense(expense);
            splits.add(split);
        }

        expense.setSplits(splits);
        expenseRepository.save(expense);

        // ✅ NEW: update UserBalance for each split
        for (Split split : splits) {
            userBalanceService.addDebt(
                    split.getUser().getId(),
                    paidBy.getId(),
                    request.getGroupId(),
                    split.getAmount()
            );
        }

        return expense;
    }
}

