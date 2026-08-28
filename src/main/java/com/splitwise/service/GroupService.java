package com.splitwise.service;

import com.splitwise.dto.GroupResponse;
import com.splitwise.entity.ExpenseGroup;
import com.splitwise.entity.GroupMember;
import com.splitwise.entity.User;
import com.splitwise.exception.ForbiddenException;
import com.splitwise.exception.ResourceNotFoundException;
import com.splitwise.repository.ExpenseGroupRepository;
import com.splitwise.repository.GroupMemberRepository;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GroupService {
    private final ExpenseGroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final UserService userService;

    public GroupService(
        ExpenseGroupRepository groupRepository,
        GroupMemberRepository memberRepository,
        UserService userService
    ) {
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
        this.userService = userService;
    }

    @Transactional
    public GroupResponse create(String name, String actorUsername) {
        User creator = userService.requireByUsername(actorUsername);
        ExpenseGroup group = groupRepository.save(new ExpenseGroup(name.trim(), creator.getId()));
        memberRepository.save(new GroupMember(group.getId(), creator.getId()));
        return GroupResponse.from(group);
    }

    @Transactional
    public List<Long> addMembers(Long groupId, List<Long> requestedUserIds, String actorUsername) {
        ExpenseGroup group = requireGroup(groupId);
        User actor = userService.requireByUsername(actorUsername);
        if (!group.getCreatedBy().equals(actor.getId())) {
            throw new ForbiddenException("Only the group creator can add members");
        }

        Set<Long> userIds = new LinkedHashSet<>(requestedUserIds);
        userService.requireAllById(userIds, userIds.size());
        List<GroupMember> newMembers = userIds.stream()
            .filter(userId -> !memberRepository.existsByGroupIdAndUserId(groupId, userId))
            .map(userId -> new GroupMember(groupId, userId))
            .toList();
        memberRepository.saveAll(newMembers);
        return newMembers.stream().map(GroupMember::getUserId).toList();
    }

    @Transactional(readOnly = true)
    public ExpenseGroup requireGroup(Long groupId) {
        return groupRepository.findById(groupId)
            .orElseThrow(() -> new ResourceNotFoundException("Group " + groupId + " not found"));
    }

    @Transactional(readOnly = true)
    public void requireMembership(Long groupId, Long userId) {
        requireGroup(groupId);
        if (!memberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new ForbiddenException("User " + userId + " is not a member of group " + groupId);
        }
    }
}
