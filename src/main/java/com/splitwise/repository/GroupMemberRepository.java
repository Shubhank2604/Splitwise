package com.splitwise.repository;

import com.splitwise.entity.GroupMember;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    boolean existsByGroupIdAndUserId(Long groupId, Long userId);

    List<GroupMember> findByGroupId(Long groupId);

    @Query("select gm.groupId from GroupMember gm where gm.userId = :userId")
    List<Long> findGroupIdsByUserId(@Param("userId") Long userId);
}
