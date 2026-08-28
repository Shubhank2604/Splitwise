package com.splitwise.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record AddGroupMembersRequest(@NotEmpty List<@NotNull Long> userIds) {
}
