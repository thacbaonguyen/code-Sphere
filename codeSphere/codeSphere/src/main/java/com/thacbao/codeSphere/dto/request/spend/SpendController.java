package com.thacbao.codeSphere.dto.request.spend;

import com.thacbao.codeSphere.configurations.CustomUserDetailsService;
import com.thacbao.codeSphere.data.repository.course.OrderRepository;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.course.SpendByMothResponse;
import com.thacbao.codeSphere.dto.response.course.SpendResponse;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/spend")
public class SpendController {

    private final OrderRepository orderRepository;
    private final CustomUserDetailsService userDetailsService;

    @GetMapping("/by-day")
    public ResponseEntity<ApiResponse> spendByDay() {
        List<SpendResponse> response = orderRepository.totalSpendByDay(userDetailsService.getUserDetails().getId());
        return CodeSphereResponses.generateResponse(response, "Spend by day success", HttpStatus.OK);
    }

    @GetMapping("/by-month")
    public ResponseEntity<ApiResponse> spendByMonth() {
        List<SpendByMothResponse> response = orderRepository.totalSpendByMonth(userDetailsService.getUserDetails().getId());
        return CodeSphereResponses.generateResponse(response, "Spend by month success", HttpStatus.OK);
    }
}
