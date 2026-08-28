package com.splitwise;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.splitwise.dto.GroupResponse;
import com.splitwise.entity.User;
import com.splitwise.exception.ForbiddenException;
import com.splitwise.repository.GroupMemberRepository;
import com.splitwise.repository.UserRepository;
import com.splitwise.service.GroupService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GroupAuthorizationIntegrationTest {
    @Autowired
    private GroupService groupService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupMemberRepository memberRepository;

    @Test
    void onlyCreatorCanAddMembers() {
        User alice = userRepository.save(new User("alice", "alice@example.com", "hash"));
        User bob = userRepository.save(new User("bob", "bob@example.com", "hash"));
        User charlie = userRepository.save(new User("charlie", "charlie@example.com", "hash"));
        GroupResponse group = groupService.create("Trip", "alice");

        assertThatThrownBy(() -> groupService.addMembers(group.id(), List.of(charlie.getId()), "bob"))
            .isInstanceOf(ForbiddenException.class);

        assertThat(groupService.addMembers(group.id(), List.of(bob.getId(), charlie.getId()), "alice"))
            .containsExactly(bob.getId(), charlie.getId());
        assertThat(memberRepository.findByGroupId(group.id())).hasSize(3);
        assertThat(memberRepository.existsByGroupIdAndUserId(group.id(), alice.getId())).isTrue();
    }
}
