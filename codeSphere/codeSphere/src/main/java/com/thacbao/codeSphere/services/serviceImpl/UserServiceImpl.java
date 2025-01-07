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
import com.thacbao.codeSphere.dto.request.UserUdRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.UserDTO;
import com.thacbao.codeSphere.entity.User;
import com.thacbao.codeSphere.exceptions.*;
import com.thacbao.codeSphere.repositories.UserRepository;
import com.thacbao.codeSphere.services.UserService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import com.thacbao.codeSphere.utils.EmailUtilService;
import com.thacbao.codeSphere.utils.OtpUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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
import java.util.concurrent.TimeUnit;

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
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public ResponseEntity<ApiResponse> signup(UserRequest userRequest) throws SQLDataException {
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
            return CodeSphereResponses.generateResponse(null, "OTP sent to " + userRequest.getEmail()
                    + ", please verify to complete registration", HttpStatus.OK);
        }
        catch (MessagingException ex){
            throw new EmailSenderException(CodeSphereConstants.EMAIL_SENDER_ERROR);
        }
        catch (Exception ex){
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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
            clearCache("allUser:admin"); // khi user duoc active -> clear cache cua get all user
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
            throw new EmailSenderException(CodeSphereConstants.EMAIL_SENDER_ERROR);
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
                String token = jwtUtils.generateToken(username, createClaim());
                return new ResponseEntity<>("{\"token\":\"" + token + "\"}", HttpStatus.OK);
            }
            return CodeSphereResponses.generateResponse(null, "Authentication failed", HttpStatus.FORBIDDEN);
        }
        catch (Exception ex){
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> getProfile() {
        String cacheKey = "updateProfile:user:" + jwtFilter.getCurrentUsername(); // khai bao cache key
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        try{
            UserDTO cacheUser = (UserDTO) valueOperations.get(cacheKey);// kiem tra cache
            if(cacheUser != null){
                System.out.println("caching profile: " + cacheKey);
                return CodeSphereResponses.generateResponse(cacheUser, "Profile updated successfully", HttpStatus.OK);
            }
            UserDTO userDTO = userDao.getProfile(jwtFilter.getCurrentUsername()); // thuc hien truy van moi neu cache rong
            valueOperations.set(cacheKey, userDTO, 24, TimeUnit.HOURS);
            return CodeSphereResponses.generateResponse
                    (userDTO, "Get profile successfully", HttpStatus.OK);
        }
        catch (Exception ex){
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> getAllUser() {
        String cacheKey = "allUser:admin";
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        try{
            if(jwtFilter.isAdmin()){
                List<UserDTO> cacheUsers = (List<UserDTO>) valueOperations.get(cacheKey);
                if(cacheUsers != null){
                    System.out.println("caching all users");
                    return CodeSphereResponses.generateResponse(cacheUsers, "Get all user successfully", HttpStatus.OK);
                }
                List<UserDTO> users = userDao.getAllUser();
                valueOperations.set(cacheKey, users, 24, TimeUnit.HOURS);
                return CodeSphereResponses.generateResponse(users, "Get all user successfully", HttpStatus.OK);
            }
            else{
                return CodeSphereResponses.generateResponse(null, CodeSphereConstants.PERMISSION_DENIED, HttpStatus.FORBIDDEN);
            }
        }
        catch (Exception ex){
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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
            return CodeSphereResponses.generateResponse
                    (null, "OTP sent to " + request.get("email") + ", please verify to reset password", HttpStatus.OK);
        } catch (MessagingException e) {
            throw new EmailSenderException(CodeSphereConstants.EMAIL_SENDER_ERROR);
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
                return CodeSphereResponses.generateResponse(null, "Password reset successfully", HttpStatus.OK);
            }
            else{
                throw new InvalidException("Invalid OTP or OTP expired");
            }
        }
        catch (Exception ex){
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> changePassword(Map<String, String> request) {
        User user = userRepository.findByUsername(jwtFilter.getCurrentUsername()).orElseThrow(
                () -> new NotFoundException("Can not found this user")
        );
        try{
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            if(passwordEncoder.matches(request.get("oldPassword"), user.getPassword())){
                user.setPassword(passwordEncoder.encode(request.get("newPassword")));
                userRepository.save(user);
                return CodeSphereResponses.generateResponse(null, "Password change successfully", HttpStatus.OK);
            }
            else{
                return CodeSphereResponses.generateResponse(null, "Old password is incorrect", HttpStatus.BAD_REQUEST);
            }
        }
        catch (Exception ex){
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> updateProfile(UserUdRequest request) {
        User user = userRepository.findByUsername(jwtFilter.getCurrentUsername()).orElseThrow(
                () -> new NotFoundException("Can not found this user")
        );
        try{
            String cacheKey = "updateProfile:user:" + jwtFilter.getCurrentUsername();
            userDao.updateUser(request, user.getId());
            clearCache(cacheKey);
            return CodeSphereResponses.generateResponse(null, "Profile updated successfully", HttpStatus.OK);
        }
        catch (Exception ex){
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> checkToken() {
        return CodeSphereResponses.generateResponse(null, "Check success", HttpStatus.OK);
    }

    private Map<String, Object> createClaim() throws SQLDataException {
        Map<String, Object> claim = new HashMap<>();
        List<String> roles = userDao.getRolesUser(customUserDetailsService.getUserDetails().getId());
        claim.put("name", customUserDetailsService.getUserDetails().getFullName());
        claim.put("role", roles);
        claim.put("dob", customUserDetailsService.getUserDetails().getDob().toString());
        claim.put("email", customUserDetailsService.getUserDetails().getEmail());
        claim.put("phoneNumber", customUserDetailsService.getUserDetails().getPhoneNumber());
        return claim;
    }
    private void clearCache(String cacheKey){
        System.out.println("clear cache: " + cacheKey);
        redisTemplate.delete(redisTemplate.keys(cacheKey + "*"));
    }
}
