package com.splitwise;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.splitwise.dto.CreateExpenseRequest;
import com.splitwise.dto.ExpenseResponse;
import com.splitwise.dto.SplitInput;
import com.splitwise.entity.User;
import com.splitwise.entity.UserBalance;
import com.splitwise.exception.ConflictException;
import com.splitwise.repository.ExpenseRepository;
import com.splitwise.repository.UserBalanceRepository;
import com.splitwise.repository.UserRepository;
import com.splitwise.service.ExpenseService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ExpenseFlowIntegrationTest {
    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserBalanceRepository balanceRepository;

    @Test
    void createsExpenseAndOnlyRecordsOtherParticipantsDebt() {
        User alice = userRepository.save(new User("alice", "alice@example.com", "hash"));
        User bob = userRepository.save(new User("bob", "bob@example.com", "hash"));

        ExpenseResponse response = expenseService.create(new CreateExpenseRequest(
            "Dinner",
            new BigDecimal("100.00"),
            null,
            List.of(
                new SplitInput(alice.getId(), new BigDecimal("20.00")),
                new SplitInput(bob.getId(), new BigDecimal("80.00"))
            )
        ), "alice", "expense-dinner-001");

        assertThat(response.id()).isNotNull();
        assertThat(expenseRepository.count()).isEqualTo(1);
        List<UserBalance> balances = balanceRepository.findAllForUser(bob.getId());
        assertThat(balances).singleElement().satisfies(balance -> {
            assertThat(balance.getFromUserId()).isEqualTo(bob.getId());
            assertThat(balance.getToUserId()).isEqualTo(alice.getId());
            assertThat(balance.getAmount()).isEqualByComparingTo("80.00");
        });
    }

    @Test
    void rejectsMismatchedSplitTotalBeforePersistingAnything() {
        User alice = userRepository.save(new User("alice", "alice@example.com", "hash"));
        User bob = userRepository.save(new User("bob", "bob@example.com", "hash"));

        assertThatThrownBy(() -> expenseService.create(new CreateExpenseRequest(
            "Dinner",
            new BigDecimal("100.00"),
            null,
            List.of(
                new SplitInput(alice.getId(), new BigDecimal("20.00")),
                new SplitInput(bob.getId(), new BigDecimal("70.00"))
            )
        ), "alice", "expense-dinner-002"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("add up exactly");

        assertThat(expenseRepository.count()).isZero();
        assertThat(balanceRepository.count()).isZero();
    }

    @Test
    void replaysTheOriginalExpenseWithoutApplyingDebtTwice() {
        User alice = userRepository.save(new User("alice", "alice@example.com", "hash"));
        User bob = userRepository.save(new User("bob", "bob@example.com", "hash"));
        CreateExpenseRequest request = new CreateExpenseRequest(
            "Dinner", new BigDecimal("80.00"), null,
            List.of(new SplitInput(bob.getId(), new BigDecimal("80.00")))
        );

        ExpenseResponse first = expenseService.create(request, "alice", "expense-retry-001");
        ExpenseResponse replay = expenseService.create(request, "alice", "expense-retry-001");

        assertThat(replay.id()).isEqualTo(first.id());
        assertThat(expenseRepository.count()).isEqualTo(1);
        assertThat(balanceRepository.findAllForUser(bob.getId())).singleElement()
            .extracting(UserBalance::getAmount)
            .isEqualTo(new BigDecimal("80.00"));
    }

    @Test
    void rejectsReusingAnExpenseKeyForDifferentContent() {
        User alice = userRepository.save(new User("alice", "alice@example.com", "hash"));
        User bob = userRepository.save(new User("bob", "bob@example.com", "hash"));
        CreateExpenseRequest original = new CreateExpenseRequest(
            "Dinner", new BigDecimal("80.00"), null,
            List.of(new SplitInput(bob.getId(), new BigDecimal("80.00")))
        );
        CreateExpenseRequest changed = new CreateExpenseRequest(
            "Dinner", new BigDecimal("90.00"), null,
            List.of(new SplitInput(bob.getId(), new BigDecimal("90.00")))
        );
        expenseService.create(original, "alice", "expense-reuse-001");

        assertThatThrownBy(() -> expenseService.create(changed, "alice", "expense-reuse-001"))
            .isInstanceOf(ConflictException.class)
            .hasMessageContaining("different request");

        assertThat(expenseRepository.count()).isEqualTo(1);
    }
}
