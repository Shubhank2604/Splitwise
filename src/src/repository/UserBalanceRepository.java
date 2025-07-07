package com.splitwise.repository;

import com.splitwise.entity.UserBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBalanceRepository extends JpaRepository<UserBalance, Long> {

    // Total you owe (all groups)
    @Query("SELECT COALESCE(SUM(ub.amount), 0) FROM UserBalance ub WHERE ub.fromUser.id = :userId")
    Double sumAmountUserOwes(@Param("userId") Long userId);

    // Total owed to you (all groups)
    @Query("SELECT COALESCE(SUM(ub.amount), 0) FROM UserBalance ub WHERE ub.toUser.id = :userId")
    Double sumAmountOwedToUser(@Param("userId") Long userId);

    // User's balances filtered by group
    @Query("SELECT ub FROM UserBalance ub WHERE (ub.fromUser.id = :userId OR ub.toUser.id = :userId) AND ub.group.id = :groupId")
    List<UserBalance> findByUserIdAndGroupId(@Param("userId") Long userId, @Param("groupId") Long groupId);

    @Query("""
    SELECT ub FROM UserBalance ub
    WHERE ub.fromUser.id = :fromUserId
      AND ub.toUser.id = :toUserId
      AND ((:groupId IS NULL AND ub.group IS NULL) OR (ub.group.id = :groupId))
    """)
    UserBalance findByFromUserIdAndToUserIdAndGroupId(
            @Param("fromUserId") Long fromUserId,
            @Param("toUserId") Long toUserId,
            @Param("groupId") Long groupId
    );


}
