package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.dto.request.UserLoginRequest;
import com.thacbao.codeSphere.dto.request.UserRequest;
import com.thacbao.codeSphere.dto.request.UserUpdateRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.UserDTO;
import org.springframework.http.ResponseEntity;

import java.sql.SQLDataException;
import java.util.List;
import java.util.Map;

public interface UserService {
    ResponseEntity<ApiResponse> signup(UserRequest userRequest) throws SQLDataException;
    String verifyAccount(Map<String, String> request);

    String regenerateOtp(Map<String, String> request);

    List<UserDTO> getUserDetails();

    ResponseEntity<?> login(UserLoginRequest request);

    ResponseEntity<ApiResponse> getProfile();

    ResponseEntity<ApiResponse> getAllUser();

    ResponseEntity<?> forgotPassword(Map<String, String> request);

    ResponseEntity<?> setPassword(String email, String otp, Map<String, String> request);

    ResponseEntity<ApiResponse> changePassword(Map<String, String> request);

    ResponseEntity<ApiResponse> updateProfile(UserUpdateRequest request);

    ResponseEntity<?> checkToken();
}
