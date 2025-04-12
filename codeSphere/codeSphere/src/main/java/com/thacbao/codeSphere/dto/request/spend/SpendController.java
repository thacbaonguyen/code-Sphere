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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/spend")
public class SpendController {

    private final OrderRepository orderRepository;

    @PostMapping("/by-day")
    public ResponseEntity<ApiResponse> spendByDay(@RequestBody Map<String, String> params) {
        List<SpendResponse> response = orderRepository.totalSpendByDay(params.get("dayAgo"));
        return CodeSphereResponses.generateResponse(response, "Spend by day success", HttpStatus.OK);
    }

    @GetMapping("/by-month")
    public ResponseEntity<ApiResponse> spendByMonth() {
        List<SpendByMothResponse> response = orderRepository.totalSpendByMonth();
        return CodeSphereResponses.generateResponse(response, "Spend by month success", HttpStatus.OK);
    }
}
