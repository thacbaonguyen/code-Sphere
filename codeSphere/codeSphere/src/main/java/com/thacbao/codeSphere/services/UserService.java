package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.dto.request.user.UserLoginReq;
import com.thacbao.codeSphere.dto.request.user.UserReq;
import com.thacbao.codeSphere.dto.request.user.UserUdReq;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.user.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLDataException;
import java.util.List;
import java.util.Map;

public interface UserService {
    ResponseEntity<ApiResponse> signup(UserReq userReq) throws SQLDataException;
    String verifyAccount(Map<String, String> request) throws SQLDataException;

    String regenerateOtp(Map<String, String> request);


    ResponseEntity<?> login(UserLoginReq request);

    ResponseEntity<?> refreshToken(String token);

    ResponseEntity<ApiResponse> getProfile();

    ResponseEntity<ApiResponse> uploadAvatarProfile(MultipartFile file);

    ResponseEntity<ApiResponse> viewAvatar();

    ResponseEntity<ApiResponse> getAllUser();

    ResponseEntity<ApiResponse> getAllManager();

    ResponseEntity<ApiResponse> getAllBlogger();

    ResponseEntity<ApiResponse> getAllUserBlocked();

    ResponseEntity<ApiResponse> searchUser(String search, String order, String by);

    ResponseEntity<?> forgotPassword(Map<String, String> request);

    String verifyForgotPassword(Map<String, String> request);

    ResponseEntity<?> setPassword(Map<String, String> request);

    ResponseEntity<ApiResponse> changePassword(Map<String, String> request);

    ResponseEntity<ApiResponse> updateProfile(UserUdReq request);

    ResponseEntity<?> checkToken();

    ResponseEntity<ApiResponse> blockUser(Map<String, String> request);
}
