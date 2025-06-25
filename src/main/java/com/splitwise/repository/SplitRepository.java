package com.splitwise.repository;

import com.splitwise.entity.Split;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SplitRepository extends JpaRepository<Split, Long> {

    List<Split> findByUserId(Long userId);
}
