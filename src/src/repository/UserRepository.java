package com.splitwise.repository;

import com.splitwise.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Power of Spring Data JPA - Derived Queries
    // It understands the method name and then automatically creates
    // queries to fetch data from the DB
    // We need not provide any implementation for the following 2 methods

    // Find user by username (custom query)
    Optional<User> findByUsername(String username);

    // Find user by email (custom query)
    Optional<User> findByEmail(String email);
}

