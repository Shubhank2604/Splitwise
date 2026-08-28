package com.splitwise;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void protectsProfileAndNeverReturnsPasswordHash() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"api-user","email":"api-user@example.com","password":"strong-password"}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.passwordHash").doesNotExist());

        MvcResult login = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"api-user","password":"strong-password"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token", not(blankOrNullString())))
            .andReturn();
        String token = com.jayway.jsonpath.JsonPath.read(login.getResponse().getContentAsString(), "$.token");

        mockMvc.perform(get("/api/users/me"))
            .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/users/me").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("api-user"))
            .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }
}
