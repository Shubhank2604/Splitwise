package com.splitwise.service;

import com.splitwise.domain.Money;
import com.splitwise.dto.BalanceEntry;
import com.splitwise.dto.BalanceResponse;
import com.splitwise.entity.User;
import com.splitwise.entity.UserBalance;
import com.splitwise.exception.ConflictException;
import com.splitwise.repository.UserBalanceRepository;
import com.splitwise.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserBalanceService {
    private final UserBalanceRepository balanceRepository;
    private final UserRepository userRepository;

    public UserBalanceService(UserBalanceRepository balanceRepository, UserRepository userRepository) {
        this.balanceRepository = balanceRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void addDebt(Long debtorId, Long creditorId, Long groupId, BigDecimal value) {
        if (debtorId.equals(creditorId)) {
            return;
        }
        BigDecimal amount = Money.positive(value);
        lockUserPair(debtorId, creditorId);
        UserBalance reverse = balanceRepository.findDebtForUpdate(creditorId, debtorId, groupId).orElse(null);
        if (reverse != null) {
            int comparison = reverse.getAmount().compareTo(amount);
            if (comparison > 0) {
                reverse.changeAmount(reverse.getAmount().subtract(amount));
                return;
            }
            balanceRepository.delete(reverse);
            if (comparison == 0) {
                return;
            }
            amount = amount.subtract(reverse.getAmount());
        }

        UserBalance direct = balanceRepository.findDebtForUpdate(debtorId, creditorId, groupId).orElse(null);
        if (direct == null) {
            balanceRepository.save(new UserBalance(debtorId, creditorId, groupId, amount));
        } else {
            direct.changeAmount(direct.getAmount().add(amount));
        }
    }

    @Transactional
    public void settleDebt(Long payerId, Long receiverId, Long groupId, BigDecimal value) {
        BigDecimal amount = Money.positive(value);
        lockUserPair(payerId, receiverId);
        UserBalance debt = balanceRepository.findDebtForUpdate(payerId, receiverId, groupId)
            .orElseThrow(() -> new ConflictException("No outstanding debt exists for this settlement"));
        int comparison = amount.compareTo(debt.getAmount());
        if (comparison > 0) {
            throw new ConflictException("Settlement exceeds the outstanding debt");
        }
        if (comparison == 0) {
            balanceRepository.delete(debt);
        } else {
            debt.changeAmount(debt.getAmount().subtract(amount));
        }
    }

    @Transactional(readOnly = true)
    public BalanceResponse getBalances(Long userId) {
        List<UserBalance> balances = balanceRepository.findAllForUser(userId);
        Set<Long> otherIds = balances.stream()
            .map(balance -> balance.getFromUserId().equals(userId)
                ? balance.getToUserId()
                : balance.getFromUserId())
            .collect(Collectors.toSet());
        Map<Long, User> users = userRepository.findAllById(otherIds).stream()
            .collect(Collectors.toMap(User::getId, Function.identity()));

        List<BalanceEntry> entries = balances.stream().map(balance -> {
            boolean userIsCreditor = balance.getToUserId().equals(userId);
            Long otherId = userIsCreditor ? balance.getFromUserId() : balance.getToUserId();
            BigDecimal signedAmount = userIsCreditor ? balance.getAmount() : balance.getAmount().negate();
            User other = users.get(otherId);
            return new BalanceEntry(otherId, other.getUsername(), balance.getGroupId(), signedAmount);
        }).toList();

        BigDecimal net = entries.stream()
            .map(BalanceEntry::balance)
            .reduce(BigDecimal.ZERO.setScale(2), BigDecimal::add);
        return new BalanceResponse(net, entries);
    }

    @Transactional(readOnly = true)
    public BigDecimal getNetBalanceForGroup(Long userId, Long groupId) {
        return balanceRepository.findAllForUserAndGroup(userId, groupId).stream()
            .map(balance -> balance.getToUserId().equals(userId)
                ? balance.getAmount()
                : balance.getAmount().negate())
            .reduce(BigDecimal.ZERO.setScale(2), BigDecimal::add);
    }

    private void lockUserPair(Long firstUserId, Long secondUserId) {
        List<Long> ids = firstUserId < secondUserId
            ? List.of(firstUserId, secondUserId)
            : List.of(secondUserId, firstUserId);
        if (userRepository.lockByIds(ids).size() != 2) {
            throw new IllegalArgumentException("Both users must exist");
        }
    }
}
