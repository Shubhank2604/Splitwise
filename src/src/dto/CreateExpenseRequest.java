package com.splitwise.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateExpenseRequest {
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPaidByUserId() {
        return paidByUserId;
    }

    public void setPaidByUserId(Long paidByUserId) {
        this.paidByUserId = paidByUserId;
    }

    public List<Long> getInvolvedUserIds() {
        return involvedUserIds;
    }

    public void setInvolvedUserIds(List<Long> involvedUserIds) {
        this.involvedUserIds = involvedUserIds;
    }

    public List<Double> getSplitAmounts() {
        return splitAmounts;
    }

    public void setSplitAmounts(List<Double> splitAmounts) {
        this.splitAmounts = splitAmounts;
    }

    private Double amount;
    private String description;
    private Long paidByUserId;
    private List<Long> involvedUserIds;
    private List<Double> splitAmounts; // same order as involvedUserIds

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    private Long groupId; // 👈 new field, optional
}

