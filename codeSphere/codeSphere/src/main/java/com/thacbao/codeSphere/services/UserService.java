package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.dto.request.UserRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<ApiResponse> signup(UserRequest userRequest);
}
