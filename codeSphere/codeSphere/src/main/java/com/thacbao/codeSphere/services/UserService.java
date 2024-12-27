package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.dto.request.UserRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import javax.mail.MessagingException;
import java.util.Map;

public interface UserService {
    ResponseEntity<ApiResponse> signup(UserRequest userRequest);
    String verifyAccount(Map<String, String> request);

    String regenerateOtp(Map<String, String> request);
}
