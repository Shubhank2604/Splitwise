package com.splitwise.repository;

import com.splitwise.entity.Group;
import com.splitwise.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    List<GroupMember> findByGroupId(Long groupId);

    @Query("SELECT DISTINCT gm.groupId FROM GroupMember gm WHERE gm.userId = :userId")
    List<Long> findGroupIdsByUserId(@Param("userId") Long userId);
}

