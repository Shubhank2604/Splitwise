package com.splitwise.dto;

public class GroupBalanceDTO {
    public GroupBalanceDTO(Long groupId, String groupName, Double youOwe, Double owedToYou) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.youOwe = youOwe;
        this.owedToYou = owedToYou;
    }

    private Long groupId;
    private String groupName;
    private Double youOwe;
    private Double owedToYou;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Double getYouOwe() {
        return youOwe;
    }

    public void setYouOwe(Double youOwe) {
        this.youOwe = youOwe;
    }

    public Double getOwedToYou() {
        return owedToYou;
    }

    public void setOwedToYou(Double owedToYou) {
        this.owedToYou = owedToYou;
    }
}

