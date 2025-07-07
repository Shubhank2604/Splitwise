package com.splitwise.dto;

import java.util.List;

public class AddGroupMembersRequest {
    private List<Long> userIds;
    // Getter & Setter

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }
}
