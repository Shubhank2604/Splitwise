package com.splitwise.repository;

import com.splitwise.entity.ExpenseGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseGroupRepository extends JpaRepository<ExpenseGroup, Long> {
}
