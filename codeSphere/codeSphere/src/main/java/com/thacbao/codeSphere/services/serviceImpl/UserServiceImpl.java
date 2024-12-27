package com.thacbao.codeSphere.services.serviceImpl;

import com.thacbao.codeSphere.constants.CodeSphereConstants;
import com.thacbao.codeSphere.dto.request.UserRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.CodeSphereResponse;
import com.thacbao.codeSphere.entity.User;
import com.thacbao.codeSphere.exceptions.UserAlreadyException;
import com.thacbao.codeSphere.repositories.UserRepository;
import com.thacbao.codeSphere.services.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final ModelMapper modelMapper;
    @Override
    public ResponseEntity<ApiResponse> signup(UserRequest userRequest) {
        Optional<User> user = userRepository.findByUsername(userRequest.getUsername());
        if(!user.isEmpty()){
            throw new UserAlreadyException("User already exists");
        }
        try{
            if(!userRequest.getPassword().equals(userRequest.getRetypePassword())){
                throw new UserAlreadyException("Password not match");
            }
            User newUser = modelMapper.map(userRequest, User.class);
            userRepository.save(newUser);
            return CodeSphereResponse.generateResponse(new ApiResponse
                    (CodeSphereConstants.SUCCESS, "Create user successfully", newUser.getUsername()), HttpStatus.OK);
        }
        catch (Exception ex){
            return CodeSphereResponse.generateResponse(new ApiResponse
                    (CodeSphereConstants.ERROR, ex.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }
}
