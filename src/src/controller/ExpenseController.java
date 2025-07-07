package com.splitwise.controller;

import com.splitwise.dto.CreateExpenseRequest;
import com.splitwise.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<String> createExpense(@RequestBody CreateExpenseRequest request) {
        expenseService.createExpense(request);
        return ResponseEntity.ok("Expense created successfully");
    }
}

