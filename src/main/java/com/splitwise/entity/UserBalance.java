package com.splitwise.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "user_balances")
public class UserBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_user_id", nullable = false)
    private Long fromUserId;

    @Column(name = "to_user_id", nullable = false)
    private Long toUserId;

    @Column(name = "group_id")
    private Long groupId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    protected UserBalance() {
    }

    public UserBalance(Long fromUserId, Long toUserId, Long groupId, BigDecimal amount) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.groupId = groupId;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void changeAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
