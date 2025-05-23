package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.dto.request.course.OrderConfirmRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.entities.core.Course;
import com.thacbao.codeSphere.entities.reference.Order;
import org.springframework.http.ResponseEntity;

public interface OrderService {
    Order createOrder();

    void saveOrder(Order order);

    Order createFreeOrder(Course course);

    ResponseEntity<ApiResponse> updateStatus(OrderConfirmRequest request);
}
