package com.thacbao.codeSphere.services.serviceImpl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.thacbao.codeSphere.configurations.CustomUserDetailsService;
import com.thacbao.codeSphere.configurations.JwtFilter;
import com.thacbao.codeSphere.configurations.JwtUtils;
import com.thacbao.codeSphere.constants.CodeSphereConstants;
import com.thacbao.codeSphere.enums.RoleEnum;
import com.thacbao.codeSphere.dao.AuthorizationDao;
import com.thacbao.codeSphere.dao.UserDao;
import com.thacbao.codeSphere.dto.request.UserLoginReq;
import com.thacbao.codeSphere.dto.request.UserReq;
import com.thacbao.codeSphere.dto.request.UserUdReq;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.BinaryCodec;
import org.apache.commons.codec.binary.Hex;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.sql.SQLDataException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
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
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketAvatar}")
    private String bucket;

    @Override
    public ResponseEntity<ApiResponse> signup(UserReq userReq) throws SQLDataException {
        Optional<User> user = userRepository.findByUsername(userReq.getUsername());
        Optional<User> userByEmail = userRepository.findByEmail(userReq.getEmail());
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
            emailUtilService.sentOtpEmail(userReq.getEmail(), OTP);
            // pw encoder
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            userReq.setPassword(passwordEncoder.encode(userReq.getPassword()));
            // new user
            User newUser = modelMapper.map(userReq, User.class);
            newUser.setOTP(OTP);
            newUser.setOtpGenerateTime(LocalDateTime.now());
            newUser.setCreatedAt(LocalDate.now());
            newUser.setUpdatedAt(LocalDate.now());
            newUser.setIsActive(false);
            userRepository.save(newUser);
            // return
            return CodeSphereResponses.generateResponse(null, "OTP sent to " + userReq.getEmail()
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
    public ResponseEntity<?> login(UserLoginReq request) {
        String cacheKey = "user:jwt:" + request.getUsername();
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
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
                String cacheToken = (String) ops.get(cacheKey);
                if(cacheToken != null){
                    log.info("cache jwt token {}", cacheKey);
                    return new ResponseEntity<>("{\"token\":\"" + cacheToken + "\"}", HttpStatus.OK);
                }
                String username = customUserDetailsService.getUserDetails().getUsername();
                String token = jwtUtils.generateToken(username, createClaim());
                ops.set(cacheKey, token, 24, TimeUnit.HOURS);
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
                log.info("caching profile: {} ", cacheKey);
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
    public ResponseEntity<ApiResponse> uploadAvatarProfile(MultipartFile file) {
        User user = userRepository.findByUsername(jwtFilter.getCurrentUsername()).orElseThrow(
                () -> new NotFoundException("Can not found this user")
        );
        try {
            if(file == null ){
                return CodeSphereResponses.generateResponse(null, "Not found file", HttpStatus.BAD_REQUEST);
            }
            if (file.getSize() == 0){
                return CodeSphereResponses.generateResponse(null, "File is empty", HttpStatus.BAD_REQUEST);
            }
            String contentType = file.getContentType();
            if (!contentType.startsWith("image/")) {
                return CodeSphereResponses.generateResponse(null, "File is not an image", HttpStatus.BAD_REQUEST);
            }
            long maxSize = 5 * 1024 * 1024;
            if (file.getSize() > maxSize) {
                return CodeSphereResponses.generateResponse(null, "File is too large", HttpStatus.BAD_REQUEST);
            }
            String oldFileName = user.getAvatar();
            String fileName = uploadToS3(file);
            user.setAvatar(fileName);
            userRepository.save(user);
            if (oldFileName != null) {
                deleteToS3(oldFileName);
            }
            return CodeSphereResponses.generateResponse(fileName, "Avatar upload successfully", HttpStatus.OK);
        }
        catch (Exception ex){
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> viewAvatar() {
        User user = userRepository.findByUsername(jwtFilter.getCurrentUsername()).orElseThrow(
                () -> new NotFoundException("Can not found this user")
        );
        try{
            S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucket, user.getAvatar()));
            byte[] bytes = s3Object.getObjectContent().readAllBytes();// lay dang bytes

            String base64 = Base64.getEncoder().encodeToString(bytes);// decode sang base64

            return CodeSphereResponses.generateResponse(base64, "Avatar view successfully", HttpStatus.OK);
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
                    log.info("caching all users: {}", cacheKey);
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
    public ResponseEntity<ApiResponse> updateProfile(UserUdReq request) {
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
        log.info("clear cache {} ", cacheKey);
        redisTemplate.delete(redisTemplate.keys(cacheKey + "*"));
    }

    private String uploadToS3(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "-" + LocalDate.now().toString() + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        amazonS3.putObject(bucket, fileName, file.getInputStream(), metadata);
        return fileName;
    }
    private void deleteToS3(String oldFileName) {
        amazonS3.deleteObject(bucket, oldFileName);
    }
}
