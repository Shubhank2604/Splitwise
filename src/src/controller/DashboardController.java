package com.splitwise.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.splitwise.dto.DashboardResponseDTO;
import com.splitwise.service.DashboardService;


@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    public DashboardResponseDTO getDashboard(@RequestParam Long userId) {
        return dashboardService.getDashboard(userId);
    }
}
