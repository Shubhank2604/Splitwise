package com.splitwise.dto;

import com.splitwise.entity.Expense;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ExpenseDTO {
    private Long id;
    private String description;
    private Double amount;
    private Long paidBy;
    private Date date;
    private List<SplitDTO> splits;

    public ExpenseDTO(Expense expense) {
        this.id = expense.getId();
        this.description = expense.getDescription();
        this.amount = expense.getAmount();
        this.paidBy = expense.getPaidBy().getId();
        this.date = expense.getDate();
        this.splits = expense.getSplits().stream()
                .map(SplitDTO::new)
                .collect(Collectors.toList());
    }
}

