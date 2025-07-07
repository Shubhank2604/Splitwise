package com.splitwise.controller;

import com.splitwise.dto.AddGroupMembersRequest;
import com.splitwise.dto.CreateGroupRequest;
import com.splitwise.entity.Group;
import com.splitwise.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @PostMapping
    public ResponseEntity<Group> createGroup(@RequestBody CreateGroupRequest request) {
        Group group = groupService.createGroup(request.getName(), request.getCreatedBy());
        return ResponseEntity.status(HttpStatus.CREATED).body(group);
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<String> addMembersToGroup(@PathVariable Long groupId, @RequestBody AddGroupMembersRequest request) {
        groupService.addMembers(groupId, request.getUserIds());
        return ResponseEntity.ok("Users added to group successfully");
    }
}

