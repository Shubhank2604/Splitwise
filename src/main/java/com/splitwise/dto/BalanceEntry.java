package com.splitwise.dto;

import java.math.BigDecimal;

public record BalanceEntry(Long otherUserId, String username, Long groupId, BigDecimal balance) {
}
