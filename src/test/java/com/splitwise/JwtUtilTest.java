package com.splitwise;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.splitwise.security.JwtUtil;
import org.junit.jupiter.api.Test;

class JwtUtilTest {
    @Test
    void signsAndValidatesTokenSubject() {
        JwtUtil jwtUtil = new JwtUtil("a-secret-that-is-long-enough-for-hs256-signing", 60_000);

        String token = jwtUtil.generateToken("alice");

        assertThat(jwtUtil.extractUsername(token)).isEqualTo("alice");
        assertThat(jwtUtil.isValid(token, "alice")).isTrue();
        assertThat(jwtUtil.isValid(token, "bob")).isFalse();
    }

    @Test
    void rejectsShortSecretsAtStartup() {
        assertThatThrownBy(() -> new JwtUtil("too-short", 60_000))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("32 bytes");
    }
}
