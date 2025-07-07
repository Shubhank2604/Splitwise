package com.splitwise.service;

import com.splitwise.dto.*;
import com.splitwise.entity.Group;
import com.splitwise.entity.GroupMember;
import com.splitwise.entity.UserBalance;
import com.splitwise.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class DashboardService {

    @Autowired
    private UserBalanceRepository userBalanceRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    public PersonalBalanceDTO getPersonalBalance(Long userId) {
        Double youOwe = userBalanceRepository.sumAmountUserOwes(userId);
        Double owedToYou = userBalanceRepository.sumAmountOwedToUser(userId);

        if (youOwe == null) youOwe = 0.0;
        if (owedToYou == null) owedToYou = 0.0;

        return new PersonalBalanceDTO(youOwe, owedToYou);
    }

    public List<GroupBalanceDTO> getGroupBalances(Long userId) {
        List<Long> groupIds = groupMemberRepository.findGroupIdsByUserId(userId);
        List<Group> userGroups = groupRepository.findAllById(groupIds);
        List<GroupBalanceDTO> groupBalances = new ArrayList<>();

        for (Group group : userGroups) {
            List<UserBalance> balances = userBalanceRepository.findByUserIdAndGroupId(userId, group.getId());

            double youOwe = 0.0;
            double owedToYou = 0.0;

            for (UserBalance ub : balances) {
                if (ub.getFromUser().getId().equals(userId)) {
                    youOwe += ub.getAmount();
                } else if (ub.getToUser().getId().equals(userId)) {
                    owedToYou += ub.getAmount();
                }
            }

            groupBalances.add(new GroupBalanceDTO(
                    group.getId(),
                    group.getName(),
                    youOwe,
                    owedToYou
            ));
        }

        return groupBalances;
    }

    public DashboardResponseDTO getDashboard(Long userId) {
        PersonalBalanceDTO personalBalance = getPersonalBalance(userId);
        List<GroupBalanceDTO> groupBalances = getGroupBalances(userId);
        return new DashboardResponseDTO(personalBalance, groupBalances);
    }
}
