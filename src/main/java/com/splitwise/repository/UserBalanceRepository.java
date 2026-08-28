package com.splitwise.repository;

import com.splitwise.entity.UserBalance;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserBalanceRepository extends JpaRepository<UserBalance, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select b from UserBalance b
        where b.fromUserId = :fromUserId and b.toUserId = :toUserId
          and ((:groupId is null and b.groupId is null) or b.groupId = :groupId)
        """)
    Optional<UserBalance> findDebtForUpdate(
        @Param("fromUserId") Long fromUserId,
        @Param("toUserId") Long toUserId,
        @Param("groupId") Long groupId
    );

    @Query("""
        select b from UserBalance b
        where b.fromUserId = :userId or b.toUserId = :userId
        order by b.groupId, b.id
        """)
    List<UserBalance> findAllForUser(@Param("userId") Long userId);

    @Query("""
        select b from UserBalance b
        where (b.fromUserId = :userId or b.toUserId = :userId)
          and ((:groupId is null and b.groupId is null) or b.groupId = :groupId)
        order by b.id
        """)
    List<UserBalance> findAllForUserAndGroup(
        @Param("userId") Long userId,
        @Param("groupId") Long groupId
    );
}
