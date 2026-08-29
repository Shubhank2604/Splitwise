package com.splitwise;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.splitwise.dto.SettlementRequest;
import com.splitwise.dto.SettlementResponse;
import com.splitwise.entity.User;
import com.splitwise.entity.UserBalance;
import com.splitwise.exception.ConflictException;
import com.splitwise.repository.SettlementRepository;
import com.splitwise.repository.UserBalanceRepository;
import com.splitwise.repository.UserRepository;
import com.splitwise.service.SettlementService;
import com.splitwise.service.UserBalanceService;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SettlementIdempotencyIntegrationTest {
    @Autowired private SettlementService settlementService;
    @Autowired private UserBalanceService balanceService;
    @Autowired private SettlementRepository settlementRepository;
    @Autowired private UserBalanceRepository balanceRepository;
    @Autowired private UserRepository userRepository;

    @Test
    void replayReturnsTheOriginalSettlementWithoutReducingDebtTwice() {
        User alice = userRepository.save(new User("alice", "alice@example.com", "hash"));
        User bob = userRepository.save(new User("bob", "bob@example.com", "hash"));
        balanceService.addDebt(bob.getId(), alice.getId(), null, new BigDecimal("50.00"));
        SettlementRequest request = new SettlementRequest(
            alice.getId(), null, new BigDecimal("20.00")
        );

        SettlementResponse first = settlementService.settle(request, "bob", "settlement-retry-001");
        SettlementResponse replay = settlementService.settle(request, "bob", "settlement-retry-001");

        assertThat(replay.id()).isEqualTo(first.id());
        assertThat(settlementRepository.count()).isEqualTo(1);
        assertThat(balanceRepository.findAll()).singleElement()
            .extracting(UserBalance::getAmount)
            .isEqualTo(new BigDecimal("30.00"));
    }

    @Test
    void keyReuseWithAnotherAmountIsRejected() {
        User alice = userRepository.save(new User("alice", "alice@example.com", "hash"));
        User bob = userRepository.save(new User("bob", "bob@example.com", "hash"));
        balanceService.addDebt(bob.getId(), alice.getId(), null, new BigDecimal("50.00"));
        settlementService.settle(
            new SettlementRequest(alice.getId(), null, new BigDecimal("10.00")),
            "bob", "settlement-reuse-001"
        );

        assertThatThrownBy(() -> settlementService.settle(
            new SettlementRequest(alice.getId(), null, new BigDecimal("15.00")),
            "bob", "settlement-reuse-001"
        )).isInstanceOf(ConflictException.class).hasMessageContaining("different request");

        assertThat(settlementRepository.count()).isEqualTo(1);
    }
}
