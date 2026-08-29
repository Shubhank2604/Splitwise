package com.splitwise.service;

import com.splitwise.domain.Money;
import com.splitwise.domain.Idempotency;
import com.splitwise.dto.SettlementRequest;
import com.splitwise.dto.SettlementResponse;
import com.splitwise.entity.Settlement;
import com.splitwise.entity.User;
import com.splitwise.repository.SettlementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettlementService {
    private final SettlementRepository settlementRepository;
    private final UserService userService;
    private final GroupService groupService;
    private final UserBalanceService balanceService;

    public SettlementService(
        SettlementRepository settlementRepository,
        UserService userService,
        GroupService groupService,
        UserBalanceService balanceService
    ) {
        this.settlementRepository = settlementRepository;
        this.userService = userService;
        this.groupService = groupService;
        this.balanceService = balanceService;
    }

    @Transactional
    public SettlementResponse settle(SettlementRequest request, String actorUsername, String rawIdempotencyKey) {
        User payer = userService.requireByUsername(actorUsername);
        String idempotencyKey = Idempotency.requireValidKey(rawIdempotencyKey);
        String fingerprint = Idempotency.fingerprint(
            request.receiverId() + "|" + request.groupId() + "|" + Money.positive(request.amount()).toPlainString()
        );
        Settlement existing = settlementRepository
            .findByPayerIdAndIdempotencyKey(payer.getId(), idempotencyKey)
            .orElse(null);
        if (existing != null) {
            Idempotency.requireSameRequest(existing.getRequestHash(), fingerprint);
            return SettlementResponse.from(existing);
        }
        User receiver = userService.requireById(request.receiverId());
        if (payer.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("A user cannot settle a debt with themselves");
        }
        if (request.groupId() != null) {
            groupService.requireMembership(request.groupId(), payer.getId());
            groupService.requireMembership(request.groupId(), receiver.getId());
        }

        balanceService.settleDebt(payer.getId(), receiver.getId(), request.groupId(), request.amount());
        Settlement settlement = settlementRepository.save(new Settlement(
            payer.getId(), receiver.getId(), request.groupId(), Money.positive(request.amount()),
            idempotencyKey, fingerprint
        ));
        return SettlementResponse.from(settlement);
    }
}
