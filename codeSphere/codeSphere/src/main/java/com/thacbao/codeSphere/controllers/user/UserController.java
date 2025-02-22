package com.thacbao.codeSphere.controllers.user;

import com.thacbao.codeSphere.dto.request.user.UserLoginReq;
import com.thacbao.codeSphere.dto.request.user.UserReq;
import com.thacbao.codeSphere.dto.request.user.UserUdReq;
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
    public ResponseEntity<ApiResponse> verifyAccount(@RequestBody Map<String, String> request) throws SQLDataException {

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

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String token){
        token = token.substring(7);
        return userService.refreshToken(token);
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

    @GetMapping("/all-manager")
    public ResponseEntity<?> getAllManager(){
        return userService.getAllManager();
    }

    @GetMapping("/all-blogger")
    public ResponseEntity<?> getAllBlogger(){
        return userService.getAllBlogger();
    }

    @GetMapping("/all-blocked")
    public ResponseEntity<?> getAllUserBlocked(){
        return userService.getAllUserBlocked();
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUser(@RequestParam(required = false) String search,
                                        @RequestParam(required = false) String order,
                                        @RequestParam(required = false) String by){
        return userService.searchUser(search, order, by);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request){

        return userService.forgotPassword(request);

    }

    @PostMapping("/verify-forgot-password")
    public ResponseEntity<ApiResponse> verifyForgotPassword(@RequestBody Map<String, String> request) throws SQLDataException {

        return CodeSphereResponses.generateResponse(null, userService.verifyForgotPassword(request), HttpStatus.OK);

    }

    @PutMapping("/set-password")
    public ResponseEntity<?> setPassword(@RequestBody Map<String, String> request){

        return userService.setPassword(request);

    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request){

        return userService.changePassword(request);

    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestBody UserUdReq request){

        return userService.updateProfile(request);

    }

    @GetMapping("/check-token")
    public ResponseEntity<?> checkToken(){

        return userService.checkToken();

    }

    @PutMapping("/block-user")
    public ResponseEntity<?> blockUser(@RequestBody Map<String, String> request){
        return userService.blockUser(request);
    }
}
