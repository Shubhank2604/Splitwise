package com.splitwise.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_balances")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_user_id", nullable = false)
    private User fromUser;

    @ManyToOne
    @JoinColumn(name = "to_user_id", nullable = false)
    private User toUser;

    @Column(nullable = false)
    private Double amount;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
}

