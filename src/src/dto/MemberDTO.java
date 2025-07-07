package com.splitwise.dto;

import com.splitwise.entity.User;

public class MemberDTO {
    private Long userId;
    private String name;

    public MemberDTO(User user) {
        this.userId = user.getId();
        this.name = user.getUsername();
    }
}

