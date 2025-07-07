package com.splitwise.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBalanceDTO {
    private Long userId;
    private String userName;
    private Double balance; // positive = user owes you, negative = you owe user

    public UserBalanceDTO(Long key, String s, Double amount) {
        this.userId = key;
        this.userName = s;
        this.balance = amount;
    }
    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

