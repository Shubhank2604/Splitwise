package com.splitwise.dto;

import java.math.BigDecimal;

public record GroupBalance(Long groupId, String groupName, BigDecimal netBalance) {
}
