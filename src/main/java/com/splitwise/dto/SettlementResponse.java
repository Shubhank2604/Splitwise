package com.splitwise.dto;

import com.splitwise.entity.Settlement;
import java.math.BigDecimal;
import java.time.Instant;

public record SettlementResponse(
    Long id,
    Long payerId,
    Long receiverId,
    Long groupId,
    BigDecimal amount,
    Instant createdAt
) {
    public static SettlementResponse from(Settlement settlement) {
        return new SettlementResponse(
            settlement.getId(),
            settlement.getPayerId(),
            settlement.getReceiverId(),
            settlement.getGroupId(),
            settlement.getAmount(),
            settlement.getCreatedAt()
        );
    }
}
