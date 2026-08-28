package com.splitwise.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(
    Long userId,
    String username,
    BigDecimal overallNetBalance,
    List<GroupBalance> groups
) {
}
