package com.tienda.electronicos.controller.dashboard;

import com.tienda.electronicos.dto.dashboard.DashboardResponse;
import com.tienda.electronicos.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public DashboardResponse getDashboardSummary() {
        return dashboardService.getDashboardSummary();
    }

    @GetMapping
    public String dashboard() {
        return "Dashboard activo";
    }
}
