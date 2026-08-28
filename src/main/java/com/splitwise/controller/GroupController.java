package com.splitwise.controller;

import com.splitwise.dto.AddGroupMembersRequest;
import com.splitwise.dto.CreateGroupRequest;
import com.splitwise.dto.GroupResponse;
import com.splitwise.service.GroupService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/groups")
public class GroupController {
    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    ResponseEntity<GroupResponse> create(
        @Valid @RequestBody CreateGroupRequest request,
        Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(groupService.create(request.name(), principal.getName()));
    }

    @PostMapping("/{groupId}/members")
    ResponseEntity<List<Long>> addMembers(
        @PathVariable Long groupId,
        @Valid @RequestBody AddGroupMembersRequest request,
        Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(groupService.addMembers(groupId, request.userIds(), principal.getName()));
    }
}
