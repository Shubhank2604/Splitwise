package com.splitwise.dto;

public class PersonalBalanceDTO {
    public PersonalBalanceDTO(Double totalYouOwe, Double totalOwedToYou) {
        this.totalYouOwe = totalYouOwe;
        this.totalOwedToYou = totalOwedToYou;
    }

    private Double totalYouOwe;
    private Double totalOwedToYou;

    public Double getTotalOwedToYou() {
        return totalOwedToYou;
    }

    public void setTotalOwedToYou(Double totalOwedToYou) {
        this.totalOwedToYou = totalOwedToYou;
    }

    public Double getTotalYouOwe() {
        return totalYouOwe;
    }

    public void setTotalYouOwe(Double totalYouOwe) {
        this.totalYouOwe = totalYouOwe;
    }
}

