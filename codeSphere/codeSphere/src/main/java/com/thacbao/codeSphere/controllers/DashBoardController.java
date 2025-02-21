package com.thacbao.codeSphere.controllers;

import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashBoardController {
    private final DashboardService dashboardService;

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse> overview() {
        return dashboardService.overview();
    }
}
