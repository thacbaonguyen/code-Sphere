package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.dto.request.course.CartRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface CartService {
    ResponseEntity<ApiResponse> addNewProduct(CartRequest request);

    ResponseEntity<ApiResponse> getCart();

    ResponseEntity<ApiResponse> deleteProductFromCart(Integer courseId);
}
