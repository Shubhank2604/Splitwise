package com.splitwise.service;

import com.splitwise.entity.Group;
import com.splitwise.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import com.splitwise.entity.GroupMember;
import com.splitwise.repository.GroupMemberRepository;
import java.util.ArrayList;
import java.util.List;
import com.splitwise.dto.AddGroupMembersRequest;
import com.splitwise.dto.CreateGroupRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    public Group createGroup(String name, Long createdBy) {
        Group group = new Group();
        group.setName(name);
        group.setCreatedBy(createdBy);
        group.setCreatedAt(LocalDateTime.now());
        Group savedGroup = groupRepository.save(group);

        // Reuse the addMembers method to add creator
        addMembers(savedGroup.getId(), List.of(createdBy));

        return savedGroup;
    }

    public void addMembers(Long groupId, List<Long> userIds) {
        List<GroupMember> groupMembers = new ArrayList<>();
        for (Long userId : userIds) {
            GroupMember member = new GroupMember();
            member.setGroupId(groupId);
            member.setUserId(userId);
            groupMembers.add(member);
        }
        groupMemberRepository.saveAll(groupMembers);
    }
}

