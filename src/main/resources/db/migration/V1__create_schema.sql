CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE expense_groups (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_groups_creator FOREIGN KEY (created_by) REFERENCES users (id)
);

CREATE TABLE group_members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    joined_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_group_members UNIQUE (group_id, user_id),
    CONSTRAINT fk_members_group FOREIGN KEY (group_id) REFERENCES expense_groups (id),
    CONSTRAINT fk_members_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE expenses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    paid_by BIGINT NOT NULL,
    group_id BIGINT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_expenses_payer FOREIGN KEY (paid_by) REFERENCES users (id),
    CONSTRAINT fk_expenses_group FOREIGN KEY (group_id) REFERENCES expense_groups (id)
);

CREATE TABLE expense_splits (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    expense_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    CONSTRAINT uk_expense_splits UNIQUE (expense_id, user_id),
    CONSTRAINT fk_splits_expense FOREIGN KEY (expense_id) REFERENCES expenses (id),
    CONSTRAINT fk_splits_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE user_balances (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_user_id BIGINT NOT NULL,
    to_user_id BIGINT NOT NULL,
    group_id BIGINT NULL,
    amount DECIMAL(19,2) NOT NULL,
    CONSTRAINT uk_user_balances UNIQUE (from_user_id, to_user_id, group_id),
    CONSTRAINT fk_balances_from_user FOREIGN KEY (from_user_id) REFERENCES users (id),
    CONSTRAINT fk_balances_to_user FOREIGN KEY (to_user_id) REFERENCES users (id),
    CONSTRAINT fk_balances_group FOREIGN KEY (group_id) REFERENCES expense_groups (id)
);

CREATE TABLE settlements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payer_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    group_id BIGINT NULL,
    amount DECIMAL(19,2) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_settlements_payer FOREIGN KEY (payer_id) REFERENCES users (id),
    CONSTRAINT fk_settlements_receiver FOREIGN KEY (receiver_id) REFERENCES users (id),
    CONSTRAINT fk_settlements_group FOREIGN KEY (group_id) REFERENCES expense_groups (id)
);

CREATE INDEX idx_group_members_user ON group_members (user_id);
CREATE INDEX idx_expenses_group ON expenses (group_id);
CREATE INDEX idx_balances_from_user ON user_balances (from_user_id);
CREATE INDEX idx_balances_to_user ON user_balances (to_user_id);
