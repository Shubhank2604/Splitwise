package com.splitwise.dto;

import java.util.List;

public class DashboardResponseDTO {
    public DashboardResponseDTO(PersonalBalanceDTO personalBalance, List<GroupBalanceDTO> groupBalances) {
        this.personalBalance = personalBalance;
        this.groupBalances = groupBalances;
    }

    private PersonalBalanceDTO personalBalance;
    private List<GroupBalanceDTO> groupBalances;

    public PersonalBalanceDTO getPersonalBalance() {
        return personalBalance;
    }

    public void setPersonalBalance(PersonalBalanceDTO personalBalance) {
        this.personalBalance = personalBalance;
    }

    public List<GroupBalanceDTO> getGroupBalances() {
        return groupBalances;
    }

    public void setGroupBalances(List<GroupBalanceDTO> groupBalances) {
        this.groupBalances = groupBalances;
    }
}

