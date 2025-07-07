package com.splitwise.dto;

import java.util.List;

public class GroupDetailsResponse {
    private Long id;
    private String name;
    private Long createdBy;
    private List<MemberDTO> members;
    private List<ExpenseDTO> expenses;
    // Getters & Setters
}

