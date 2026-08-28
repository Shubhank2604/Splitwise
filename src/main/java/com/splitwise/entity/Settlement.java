package com.splitwise.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "settlements")
public class Settlement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payer_id", nullable = false)
    private Long payerId;

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @Column(name = "group_id")
    private Long groupId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Settlement() {
    }

    public Settlement(Long payerId, Long receiverId, Long groupId, BigDecimal amount) {
        this.payerId = payerId;
        this.receiverId = receiverId;
        this.groupId = groupId;
        this.amount = amount;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getPayerId() {
        return payerId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
