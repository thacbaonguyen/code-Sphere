package com.thacbao.codeSphere.services.serviceImpl;

import com.thacbao.codeSphere.configurations.CustomUserDetailsService;
import com.thacbao.codeSphere.configurations.JwtFilter;
import com.thacbao.codeSphere.configurations.JwtUtils;
import com.thacbao.codeSphere.constants.CodeSphereConstants;
import com.thacbao.codeSphere.constants.RoleEnum;
import com.thacbao.codeSphere.dao.AuthorizationDao;
import com.thacbao.codeSphere.dao.UserDao;
import com.thacbao.codeSphere.dto.request.UserLoginRequest;
import com.thacbao.codeSphere.dto.request.UserRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.CodeSphereResponse;
import com.thacbao.codeSphere.dto.response.UserDTO;
import com.thacbao.codeSphere.entity.User;
import com.thacbao.codeSphere.exceptions.*;
import com.thacbao.codeSphere.repositories.UserRepository;
import com.thacbao.codeSphere.services.UserService;
import com.thacbao.codeSphere.utils.EmailUtilService;
import com.thacbao.codeSphere.utils.OtpUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.sql.SQLDataException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final OtpUtils otpUtils;

    private final EmailUtilService emailUtilService;

    private final AuthorizationDao authorizationDao;

    private final UserDao userDao;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtils jwtUtils;
    private final JwtFilter jwtFilter;

    @Override
    public ResponseEntity<ApiResponse> signup(UserRequest userRequest) {
        Optional<User> user = userRepository.findByUsername(userRequest.getUsername());
        Optional<User> userByEmail = userRepository.findByEmail(userRequest.getEmail());
        if(user.isPresent()){
            if(user.get().getIsActive()){
                throw new AlreadyException("This username already exists");
            }
            else {
                userDao.deleteUser(user.get().getId());
            }
        }
        if(userByEmail.isPresent()){
            if(userByEmail.get().getIsActive()){
                throw new AlreadyException("This email already exists");
            }
            else {
                userDao.deleteUser(userByEmail.get().getId());
            }
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
            newUser.setIsActive(false);
            userRepository.save(newUser);
            // return
            return CodeSphereResponse.generateResponse(new ApiResponse
                    (CodeSphereConstants.SUCCESS, "OTP sent to " + userRequest.getEmail() + ", please verify to complete registration",
                            null), HttpStatus.OK);
        }
        catch (MessagingException ex){
            throw new EmailSenderException("Some thing went wrong with email server, please try again later or contact admin to fix this issue");
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
            throw new EmailSenderException("Some thing went wrong with email server, please try again later or contact admin to fix this issue");
        }
        user.setOTP(otp);
        user.setOtpGenerateTime(LocalDateTime.now());
        userRepository.save(user);
        return "OTP regenerate successfully";
    }

    public List<UserDTO> getUserDetails(){

        List<UserDTO> users = userDao.getUserDetails(7);
        return users;
    }

    @Override
    public ResponseEntity<?> login(UserLoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(
                () -> new NotFoundException("Can not found this user")
        );
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        Boolean isPasswordCorrect = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if(!isPasswordCorrect){
            throw new InvalidException("Invalid password");
        }
        if(!user.getIsActive()){
            throw new PermissionException("Account is not active, please create account one more time");
        }
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            if(authentication.isAuthenticated()){
                String username = customUserDetailsService.getUserDetails().getUsername();
                Map<String, Object> claim = new HashMap<>();
                List<String> roles = userDao.getRolesUser(customUserDetailsService.getUserDetails().getId());
                claim.put("name", customUserDetailsService.getUserDetails().getFullName());
                claim.put("role", roles);
                claim.put("dob", customUserDetailsService.getUserDetails().getDob().toString());
                claim.put("email", customUserDetailsService.getUserDetails().getEmail());
                claim.put("phoneNumber", customUserDetailsService.getUserDetails().getPhoneNumber());
                String token = jwtUtils.generateToken(username, claim);
                return new ResponseEntity<>("{\"token\":\"" + token + "\"}", HttpStatus.OK);
            }
            return CodeSphereResponse.generateResponse(new ApiResponse
                    ("error", "Authentication failed", null), HttpStatus.BAD_REQUEST);
        }
        catch (Exception ex){
            return CodeSphereResponse.generateResponse(new ApiResponse
                    (CodeSphereConstants.ERROR, ex.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> getProfile() {
        try{
            return CodeSphereResponse.generateResponse(new ApiResponse
                    ("success", "Get profile successfully",
                            userDao.getProfile(jwtFilter.getCurrentUsername())), HttpStatus.OK);
        }
        catch (SQLDataException exception){
            return CodeSphereResponse.generateResponse(new ApiResponse
                    ("error", exception.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (Exception ex){
            return CodeSphereResponse.generateResponse(new ApiResponse("error", ex.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> getAllUser() {
        try{
            if(jwtFilter.isAdmin()){
                List<UserDTO> users = userDao.getAllUser();
                return CodeSphereResponse.generateResponse(new ApiResponse
                        ("success", "Get all user successfully", users), HttpStatus.OK);
            }
            else{
                throw new PermissionException("Permission required: admin, you don't have permission to access this resource");
            }
        }
        catch (Exception ex){
            return CodeSphereResponse.generateResponse(new ApiResponse
                    ("error", ex.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> forgotPassword(Map<String, String> request) {
        User user = userRepository.findByEmail(request.get("email")).orElseThrow(
                () -> new NotFoundException("Can not found this user")
        );
        String otp = otpUtils.generateOtp();
        try{
            emailUtilService.sentResetPasswordEmail(request.get("email"), otp);
            user.setOTP(otp);
            user.setOtpGenerateTime(LocalDateTime.now());
            userRepository.save(user);
            return CodeSphereResponse.generateResponse(new ApiResponse
                    ("success", "OTP sent to " + request.get("email") + ", please verify to reset password", null), HttpStatus.OK);
        } catch (MessagingException e) {
            throw new EmailSenderException("Some thing went wrong with email server, please try again later or contact admin to fix this issue");
        }
    }

    @Override
    public ResponseEntity<?> setPassword(String email, String otp, Map<String, String> request) {
        User user = userRepository.findByEmail(email).orElseThrow(
                ()-> new NotFoundException("Can not found this user")
        );
        try{
            if(user.getOTP().equals(otp) && Duration.between(user.getOtpGenerateTime(), LocalDateTime.now()).getSeconds() < (60 * 2)){
                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
                user.setPassword(passwordEncoder.encode(request.get("password")));
                userRepository.save(user);
                return CodeSphereResponse.generateResponse(new ApiResponse
                        ("success", "Password reset successfully", null), HttpStatus.OK);
            }
            else{
                throw new InvalidException("Invalid OTP or OTP expired");
            }
        }
        catch (Exception ex){
            return CodeSphereResponse.generateResponse(new ApiResponse
                    (CodeSphereConstants.ERROR, ex.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }
}
