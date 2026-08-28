package com.splitwise.dto;

import com.splitwise.entity.Expense;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ExpenseResponse(
    Long id,
    String description,
    BigDecimal amount,
    Long paidBy,
    Long groupId,
    Instant createdAt,
    List<SplitInput> splits
) {
    public static ExpenseResponse from(Expense expense) {
        return new ExpenseResponse(
            expense.getId(),
            expense.getDescription(),
            expense.getAmount(),
            expense.getPaidBy(),
            expense.getGroupId(),
            expense.getCreatedAt(),
            expense.getSplits().stream()
                .map(split -> new SplitInput(split.getUserId(), split.getAmount()))
                .toList()
        );
    }
}
