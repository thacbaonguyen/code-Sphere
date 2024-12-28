package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.dto.request.UserLoginRequest;
import com.thacbao.codeSphere.dto.request.UserRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.UserDTO;
import com.thacbao.codeSphere.entity.User;
import org.springframework.http.ResponseEntity;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Map;

public interface UserService {
    ResponseEntity<ApiResponse> signup(UserRequest userRequest);
    String verifyAccount(Map<String, String> request);

    String regenerateOtp(Map<String, String> request);

    List<UserDTO> getUserDetails();

    ResponseEntity<?> login(UserLoginRequest request);
}
