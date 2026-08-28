package com.splitwise.service;

import com.splitwise.domain.Money;
import com.splitwise.dto.CreateExpenseRequest;
import com.splitwise.dto.ExpenseResponse;
import com.splitwise.dto.SplitInput;
import com.splitwise.entity.Expense;
import com.splitwise.entity.ExpenseSplit;
import com.splitwise.entity.User;
import com.splitwise.repository.ExpenseRepository;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserService userService;
    private final GroupService groupService;
    private final UserBalanceService balanceService;

    public ExpenseService(
        ExpenseRepository expenseRepository,
        UserService userService,
        GroupService groupService,
        UserBalanceService balanceService
    ) {
        this.expenseRepository = expenseRepository;
        this.userService = userService;
        this.groupService = groupService;
        this.balanceService = balanceService;
    }

    @Transactional
    public ExpenseResponse create(CreateExpenseRequest request, String actorUsername) {
        User payer = userService.requireByUsername(actorUsername);
        BigDecimal total = Money.positive(request.amount());
        Set<Long> participantIds = new LinkedHashSet<>();
        BigDecimal splitTotal = BigDecimal.ZERO.setScale(2);
        for (SplitInput split : request.splits()) {
            if (!participantIds.add(split.userId())) {
                throw new IllegalArgumentException("Each user may appear in the split only once");
            }
            splitTotal = splitTotal.add(Money.positive(split.amount()));
        }
        if (splitTotal.compareTo(total) != 0) {
            throw new IllegalArgumentException("Split amounts must add up exactly to the expense amount");
        }
        userService.requireAllById(participantIds, participantIds.size());

        if (request.groupId() != null) {
            groupService.requireMembership(request.groupId(), payer.getId());
            participantIds.forEach(userId -> groupService.requireMembership(request.groupId(), userId));
        }

        Expense expense = new Expense(request.description().trim(), total, payer.getId(), request.groupId());
        request.splits().forEach(split ->
            expense.addSplit(new ExpenseSplit(split.userId(), Money.positive(split.amount())))
        );
        Expense saved = expenseRepository.save(expense);

        request.splits().stream()
            .filter(split -> !split.userId().equals(payer.getId()))
            .forEach(split -> balanceService.addDebt(
                split.userId(), payer.getId(), request.groupId(), split.amount()
            ));
        return ExpenseResponse.from(saved);
    }
}
