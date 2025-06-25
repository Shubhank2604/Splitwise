package com.splitwise.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "users")  // Table name in MySQL
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setExpensesPaid(List<Expense> expensesPaid) {
        this.expensesPaid = expensesPaid;
    }

    public List<Split> getSplits() {
        return splits;
    }

    public void setSplits(List<Split> splits) {
        this.splits = splits;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @JsonProperty
    private String username;

    @Column(nullable = false, unique = true)
    @JsonProperty
    private String email;

    @Column(nullable = false)
    @JsonProperty
    private String password;

    public List<Expense> getExpensesPaid() {
        return expensesPaid;
    }

    @OneToMany(mappedBy = "paidBy", cascade = CascadeType.ALL) // Always use mappedBy on the inverse (non-owning) side (i.e., User here).
    private List<Expense> expensesPaid = new ArrayList<>();

    // Add this if you want to fetch user splits too
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Split> splits = new ArrayList<>();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
