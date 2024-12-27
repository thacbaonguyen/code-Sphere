package com.thacbao.codeSphere.services.serviceImpl;

import com.thacbao.codeSphere.constants.CodeSphereConstants;
import com.thacbao.codeSphere.constants.RoleEnum;
import com.thacbao.codeSphere.dao.AuthorizationDao;
import com.thacbao.codeSphere.dto.request.UserRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.CodeSphereResponse;
import com.thacbao.codeSphere.entity.Authorization;
import com.thacbao.codeSphere.entity.Role;
import com.thacbao.codeSphere.entity.User;
import com.thacbao.codeSphere.exceptions.AlreadyException;
import com.thacbao.codeSphere.exceptions.InvalidException;
import com.thacbao.codeSphere.exceptions.NotFoundException;
import com.thacbao.codeSphere.repositories.UserRepository;
import com.thacbao.codeSphere.services.UserService;
import com.thacbao.codeSphere.utils.EmailUtilService;
import com.thacbao.codeSphere.utils.OtpUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final OtpUtils otpUtils;

    private final EmailUtilService emailUtilService;

    private final AuthorizationDao authorizationDao;

    @Override
    public ResponseEntity<ApiResponse> signup(UserRequest userRequest) {
        Optional<User> user = userRepository.findByUsername(userRequest.getUsername());
        Optional<User> userByEmail = userRepository.findByEmail(userRequest.getEmail());
        if(userByEmail.isPresent() || user.isPresent()){
            throw new AlreadyException("This username or email already exists");
        }
        if(!userRequest.getPassword().equals(userRequest.getRetypePassword())){
            throw new InvalidException("Password not match");
        }
        try{
            String OTP = otpUtils.generateOtp();
            emailUtilService.sentOtpEmail(userRequest.getEmail(), OTP);
            // pw encoder
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            // new user
            User newUser = modelMapper.map(userRequest, User.class);
            newUser.setOTP(OTP);
            newUser.setOtpGenerateTime(LocalDateTime.now());
            newUser.setCreatedAt(LocalDate.now());
            newUser.setUpdatedAt(LocalDate.now());
            userRepository.save(newUser);
            // return
            return CodeSphereResponse.generateResponse(new ApiResponse
                    (CodeSphereConstants.SUCCESS, "OTP sent to " + userRequest.getEmail() + ", please verify to complete registration",
                            null), HttpStatus.OK);
        }
        catch (Exception ex){
            return CodeSphereResponse.generateResponse(new ApiResponse
                    (CodeSphereConstants.ERROR, ex.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public String verifyAccount(Map<String, String> request) {
        User user = userRepository.findByEmail(request.get("email")).orElseThrow(
                () -> new NotFoundException("Can not found this user")
        );

        if(request.get("otp").equals(user.getOTP()) && Duration.between(user.getOtpGenerateTime(),
                LocalDateTime.now()).getSeconds() < (2* 60)){
            user.setIsActive(true);
            userRepository.save(user);
            authorizationDao.insertIntoAuthorization(user.getId(), RoleEnum.USER.getId());
            return "Account verified successfully";
        }
        else if(!request.get("otp").equals(user.getOTP())){
            return "OTP is invalid";
        }
        return "OTP expired";
    }

    @Override
    public String regenerateOtp(Map<String, String> request) {
        User user = userRepository.findByEmail(request.get("email")).orElseThrow(
                ()-> new NotFoundException("Can not found this user")
        );
        String otp = otpUtils.generateOtp();
        try {
            emailUtilService.sentOtpEmail(request.get("email"), otp);
        }
        catch (MessagingException ex){
            ex.printStackTrace();
        }
        user.setOTP(otp);
        user.setOtpGenerateTime(LocalDateTime.now());
        userRepository.save(user);
        return "OTP regenerate successfully";
    }


}
