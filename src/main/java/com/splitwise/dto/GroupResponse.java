package com.splitwise.dto;

import com.splitwise.entity.ExpenseGroup;
import java.time.Instant;

public record GroupResponse(Long id, String name, Long createdBy, Instant createdAt) {
    public static GroupResponse from(ExpenseGroup group) {
        return new GroupResponse(group.getId(), group.getName(), group.getCreatedBy(), group.getCreatedAt());
    }
}
