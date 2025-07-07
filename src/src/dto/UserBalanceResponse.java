package com.splitwise.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserBalanceResponse {
    private List<UserBalanceDTO> balances;
    private Double netBalance;

    public UserBalanceResponse(List<UserBalanceDTO> balanceDTOs, double net) {
        this.balances = balanceDTOs;
        this.netBalance = net;
    }
    public List<UserBalanceDTO> getBalances() {
        return balances;
    }

    public Double getNetBalance() {
        return netBalance;
    }
}
