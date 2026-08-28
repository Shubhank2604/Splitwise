package com.splitwise;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.splitwise.entity.User;
import com.splitwise.entity.UserBalance;
import com.splitwise.exception.ConflictException;
import com.splitwise.repository.UserBalanceRepository;
import com.splitwise.repository.UserRepository;
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
class UserBalanceServiceIntegrationTest {
    @Autowired
    private UserBalanceService balanceService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserBalanceRepository balanceRepository;

    @Test
    void netsOpposingDebtsIntoOneCanonicalDirection() {
        User alice = userRepository.save(new User("alice", "alice@example.com", "hash"));
        User bob = userRepository.save(new User("bob", "bob@example.com", "hash"));

        balanceService.addDebt(bob.getId(), alice.getId(), null, new BigDecimal("100.00"));
        balanceService.addDebt(alice.getId(), bob.getId(), null, new BigDecimal("40.00"));

        assertDebt(bob.getId(), alice.getId(), "60.00");

        balanceService.addDebt(alice.getId(), bob.getId(), null, new BigDecimal("80.00"));

        assertDebt(alice.getId(), bob.getId(), "20.00");
    }

    @Test
    void rejectsOverpaymentAndRemovesFullySettledDebt() {
        User alice = userRepository.save(new User("alice", "alice@example.com", "hash"));
        User bob = userRepository.save(new User("bob", "bob@example.com", "hash"));
        balanceService.addDebt(bob.getId(), alice.getId(), null, new BigDecimal("25.00"));

        assertThatThrownBy(() -> balanceService.settleDebt(
            bob.getId(), alice.getId(), null, new BigDecimal("25.01")
        )).isInstanceOf(ConflictException.class).hasMessageContaining("exceeds");

        balanceService.settleDebt(bob.getId(), alice.getId(), null, new BigDecimal("25.00"));
        assertThat(balanceRepository.count()).isZero();
    }

    private void assertDebt(Long fromUserId, Long toUserId, String amount) {
        assertThat(balanceRepository.findAll()).singleElement().satisfies(balance -> {
            assertThat(balance.getFromUserId()).isEqualTo(fromUserId);
            assertThat(balance.getToUserId()).isEqualTo(toUserId);
            assertThat(balance.getAmount()).isEqualByComparingTo(amount);
        });
    }
}
