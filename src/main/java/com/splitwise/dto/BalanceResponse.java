package com.splitwise.dto;

import java.math.BigDecimal;
import java.util.List;

public record BalanceResponse(BigDecimal netBalance, List<BalanceEntry> entries) {
}
