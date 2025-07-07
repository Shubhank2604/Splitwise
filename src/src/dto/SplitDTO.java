package com.splitwise.dto;

import com.splitwise.entity.Split;

public class SplitDTO {
    private Long userId;
    private Double amount;

    public SplitDTO(Split split) {
        this.userId = split.getUser().getId();
        this.amount = split.getAmount();
    }
}

