package com.splitwise.service;

import com.splitwise.dto.BalanceResponse;
import com.splitwise.dto.DashboardResponse;
import com.splitwise.dto.GroupBalance;
import com.splitwise.entity.ExpenseGroup;
import com.splitwise.entity.User;
import com.splitwise.repository.ExpenseGroupRepository;
import com.splitwise.repository.GroupMemberRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {
    private final UserService userService;
    private final UserBalanceService balanceService;
    private final GroupMemberRepository memberRepository;
    private final ExpenseGroupRepository groupRepository;

    public DashboardService(
        UserService userService,
        UserBalanceService balanceService,
        GroupMemberRepository memberRepository,
        ExpenseGroupRepository groupRepository
    ) {
        this.userService = userService;
        this.balanceService = balanceService;
        this.memberRepository = memberRepository;
        this.groupRepository = groupRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(String actorUsername) {
        User user = userService.requireByUsername(actorUsername);
        BalanceResponse balances = balanceService.getBalances(user.getId());
        List<GroupBalance> groups = memberRepository.findGroupIdsByUserId(user.getId()).stream()
            .map(groupId -> {
                ExpenseGroup group = groupRepository.findById(groupId).orElseThrow();
                return new GroupBalance(
                    groupId,
                    group.getName(),
                    balanceService.getNetBalanceForGroup(user.getId(), groupId)
                );
            })
            .toList();
        return new DashboardResponse(user.getId(), user.getUsername(), balances.netBalance(), groups);
    }
}
