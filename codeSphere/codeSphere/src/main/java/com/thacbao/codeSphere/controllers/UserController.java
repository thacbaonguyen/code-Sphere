package com.thacbao.codeSphere.controllers;

import com.thacbao.codeSphere.dto.request.UserLoginReq;
import com.thacbao.codeSphere.dto.request.UserReq;
import com.thacbao.codeSphere.dto.request.UserUdReq;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.services.UserService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.sql.SQLDataException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@Valid @RequestBody UserReq userReq, BindingResult bindingResult) throws SQLDataException {

        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            return CodeSphereResponses.generateResponse(errors, "Validation failed", HttpStatus.BAD_REQUEST);
        }
        return userService.signup(userReq);

    }

    @PostMapping("/verify-account")
    public ResponseEntity<ApiResponse> verifyAccount(@RequestBody Map<String, String> request){

            return CodeSphereResponses.generateResponse(null, userService.verifyAccount(request), HttpStatus.OK);

    }

    @PostMapping("/regenerate-otp")
    public ResponseEntity<ApiResponse> reGenerateOtp(@RequestBody Map<String, String> request){

        return CodeSphereResponses.generateResponse(null, userService.regenerateOtp(request), HttpStatus.OK);

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginReq request){

        return userService.login(request);

    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(){

        return userService.getProfile();

    }

    @PostMapping("/file/upload/avatar")
    public ResponseEntity<ApiResponse> uploadAvatar(@RequestParam("file") MultipartFile file){
        return userService.uploadAvatarProfile(file);
    }

    @GetMapping("/file/view/avatar")
    public ResponseEntity<ApiResponse> getAvatar(){
        return userService.viewAvatar();
    }

    @GetMapping("/all-user")
    public ResponseEntity<?> getAllUser(){

        return userService.getAllUser();

    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request){

        return userService.forgotPassword(request);

    }

    @PutMapping("/set-password")
    public ResponseEntity<?> setPassword(@RequestParam String email, @RequestParam String otp,
                                         @RequestBody Map<String, String> request){

        return userService.setPassword(email, otp, request);

    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request){

        return userService.changePassword(request);

    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestBody UserUdReq request){

        return userService.updateProfile(request);

    }


    @GetMapping("/test")
    public ResponseEntity<?> test(){
        return CodeSphereResponses.generateResponse(userService.getUserDetails(), "get all user", HttpStatus.OK);
    }

    @GetMapping("/check-token")
    public ResponseEntity<?> checkToken(){

        return userService.checkToken();

    }
}
