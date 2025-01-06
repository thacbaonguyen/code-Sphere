package com.thacbao.codeSphere.controllers;

import com.thacbao.codeSphere.constants.CodeSphereConstants;
import com.thacbao.codeSphere.dto.request.UserLoginRequest;
import com.thacbao.codeSphere.dto.request.UserRequest;
import com.thacbao.codeSphere.dto.request.UserUdRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.CodeSphereResponse;
import com.thacbao.codeSphere.exceptions.AlreadyException;
import com.thacbao.codeSphere.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@Valid @RequestBody UserRequest userRequest, BindingResult bindingResult) {
        try {
            if(bindingResult.hasErrors()){
                Map<String, String> errors = new HashMap<>();
                bindingResult.getFieldErrors().forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );
                return CodeSphereResponse.generateResponse(new ApiResponse
                        (CodeSphereConstants.ERROR, "Validation failed", errors), HttpStatus.BAD_REQUEST);
            }
            return userService.signup(userRequest);
        }
        catch (AlreadyException ex){
            return CodeSphereResponse.generateResponse(new ApiResponse
                    (CodeSphereConstants.ERROR, ex.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
        catch (Exception ex) {
            return CodeSphereResponse.generateResponse(new ApiResponse
                    (CodeSphereConstants.ERROR, ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/verify-account")
    public ResponseEntity<ApiResponse> verifyAccount(@RequestBody Map<String, String> request){
        try {
            return CodeSphereResponse.generateResponse(new ApiResponse
                    (CodeSphereConstants.SUCCESS, userService.verifyAccount(request), null), HttpStatus.OK);
        }
        catch (Exception ex){
            return CodeSphereResponse.generateResponse(new ApiResponse
                    (CodeSphereConstants.ERROR, ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/regenerate-otp")
    public ResponseEntity<ApiResponse> reGenerateOtp(@RequestBody Map<String, String> request){
        try {
            return CodeSphereResponse.generateResponse(new ApiResponse
                    (CodeSphereConstants.SUCCESS, userService.regenerateOtp(request), null), HttpStatus.OK);
        }
        catch (Exception ex){
            return CodeSphereResponse.generateResponse(new ApiResponse
                    (CodeSphereConstants.ERROR, ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest request){
        try {
            return userService.login(request);
        }
        catch (Exception ex){
            return CodeSphereResponse.generateResponse(new ApiResponse
                    (CodeSphereConstants.ERROR, ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(){
        try {
            return userService.getProfile();
        }
        catch (Exception ex){
            return CodeSphereResponse.generateResponse(new ApiResponse
                    (CodeSphereConstants.ERROR, ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all-user")
    public ResponseEntity<?> getAllUser(){
        try {
            return userService.getAllUser();
        }
        catch (Exception ex){
            return CodeSphereResponse.generateResponse(new ApiResponse
                    (CodeSphereConstants.ERROR, ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request){
        try{
            return userService.forgotPassword(request);
        }
        catch (Exception ex){
            return CodeSphereResponse.generateResponse(new ApiResponse
                    (CodeSphereConstants.ERROR, ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/set-password")
    public ResponseEntity<?> setPassword(@RequestParam String email, @RequestParam String otp,
                                         @RequestBody Map<String, String> request){
        try{
            return userService.setPassword(email, otp, request);
        }
        catch (Exception ex){
            return CodeSphereResponse.generateResponse(new ApiResponse
                    (CodeSphereConstants.ERROR, ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request){
        try{
            return userService.changePassword(request);
        }
        catch (Exception ex){
            return CodeSphereResponse.generateResponse(new ApiResponse
                    (CodeSphereConstants.ERROR, ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestBody UserUdRequest request){
        try{
            return userService.updateProfile(request);
        }
        catch (Exception ex){
            return CodeSphereResponse.generateResponse(new ApiResponse
                    (CodeSphereConstants.ERROR, ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/test")
    public ResponseEntity<?> test(){
        return CodeSphereResponse.generateResponse(new ApiResponse
                ("success", "get all user", userService.getUserDetails()), HttpStatus.OK);
    }

    @GetMapping("/check-token")
    public ResponseEntity<?> checkToken(){
        try {
            return userService.checkToken();
        }
        catch (Exception ex){
            return CodeSphereResponse.generateResponse(new ApiResponse
                    (CodeSphereConstants.ERROR, ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
