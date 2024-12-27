package com.thacbao.codeSphere.controllers;

import com.thacbao.codeSphere.constants.CodeSphereConstants;
import com.thacbao.codeSphere.dto.request.UserRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.CodeSphereResponse;
import com.thacbao.codeSphere.exceptions.UserAlreadyException;
import com.thacbao.codeSphere.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
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
        catch (UserAlreadyException ex){
            return CodeSphereResponse.generateResponse(new ApiResponse
                    (CodeSphereConstants.ERROR, ex.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
        catch (Exception ex) {
            return CodeSphereResponse.generateResponse(new ApiResponse
                    (CodeSphereConstants.ERROR, "Internal server error", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
