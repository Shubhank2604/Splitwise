package com.splitwise.controller;

import com.splitwise.dto.SettlementRequest;
import com.splitwise.dto.SettlementResponse;
import com.splitwise.service.SettlementService;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settlements")
public class SettlementController {
    private final SettlementService settlementService;

    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @PostMapping
    ResponseEntity<SettlementResponse> settle(
        @Valid @RequestBody SettlementRequest request,
        @RequestHeader("Idempotency-Key") String idempotencyKey,
        Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(settlementService.settle(request, principal.getName(), idempotencyKey));
    }
}
