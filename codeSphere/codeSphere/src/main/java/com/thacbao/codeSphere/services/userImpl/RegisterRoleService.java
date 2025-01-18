package com.thacbao.codeSphere.services.userImpl;

import com.thacbao.codeSphere.configurations.JwtFilter;
import com.thacbao.codeSphere.constants.CodeSphereConstants;
import com.thacbao.codeSphere.data.dao.AuthorizationDao;
import com.thacbao.codeSphere.data.repository.user.RegisterRoleRepository;
import com.thacbao.codeSphere.data.repository.user.RoleRepository;
import com.thacbao.codeSphere.data.repository.user.UserRepository;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.user.RegisterRoleDTO;
import com.thacbao.codeSphere.entities.core.User;
import com.thacbao.codeSphere.entities.reference.RegisterRole;
import com.thacbao.codeSphere.entities.reference.Role;
import com.thacbao.codeSphere.exceptions.common.NotFoundException;
import com.thacbao.codeSphere.exceptions.user.PermissionException;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterRoleService {

    private final UserRepository userRepository;
    private final JwtFilter jwtFilter;
    private final RegisterRoleRepository registerRoleRepository;
    private final RoleRepository roleRepository;
    private final AuthorizationDao authorizationDao;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Không thể đăng kí quyền admin
     * Nếu request với roleID đã được gửi trước đó thì sẽ ném ra  already
     * nếu bypass tất cả, request được gửi chờ response từ admin
     * @param request: roleId
     * @return
     */
    public ResponseEntity<ApiResponse> sendRequestRegisterRole(Map<String, String > request){
        try{
            User user = userRepository.findByUsername(jwtFilter.getCurrentUsername()).orElseThrow(
                    () -> new NotFoundException("User not found")
            );
            Role role = roleRepository.findById(Integer.parseInt(request.get("roleId")))
                    .orElseThrow(() -> new NotFoundException("Role not found"));
            if (role.getName().equalsIgnoreCase("admin")){
                throw new PermissionException("You are not allowed to register admin role");
            }
            RegisterRole existsByUser = registerRoleRepository.findByUserAndRole(user, role);
            if (existsByUser != null) {
                return CodeSphereResponses.generateResponse(null, "This request already send!", HttpStatus.CONFLICT);
            }
            RegisterRole registerRole = new RegisterRole();
            registerRole.setUser(user);
            registerRole.setRole(role);
            registerRole.setAccepted(false);
            registerRoleRepository.save(registerRole);
            return CodeSphereResponses.generateResponse(null,
                    "Send request successfully, please wait for admin accepted!", HttpStatus.CREATED);
        }
        catch (Exception ex){
            log.error("logging error with message {}",ex.getMessage(), ex.getCause());
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Lấy ra tất cả các request, bao gồm id, role_id, user_id, status -> update api
     * @return
     */
    public ResponseEntity<ApiResponse> getAllRequestRegisterRole(){
        if (jwtFilter.isAdmin()){
            Pageable pageable = PageRequest.of(0, 20);
            Page<RegisterRole> registerRoles = registerRoleRepository.findAll(pageable);
            Page<RegisterRoleDTO> result = registerRoles.map(RegisterRoleDTO::new);
            return CodeSphereResponses.generateResponse(result, "All request register roles successfully", HttpStatus.OK);
        }
        else
            throw new PermissionException(CodeSphereConstants.PERMISSION_DENIED);
    }

    /**
     * Nếu request được chấp nhận, gọi authorizationDao để tạo role mới -> xóa cache get all user by admin
     * Nếu bị reject, xóa role đã có trong db -> set status request về false
     * @param id
     * @param request
     * @return
     * @throws SQLDataException
     */
    public ResponseEntity<ApiResponse> activateRoleForUser(Integer id, Map<String, String> request) throws SQLDataException {

        RegisterRole registerRole = registerRoleRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Role not found")
        );
        if (jwtFilter.isAdmin()){
            if (request.get("isAccepted").equalsIgnoreCase("true")){
                authorizationDao.insertIntoAuthorization(registerRole.getUser().getId(), registerRole.getRole().getId());
                redisTemplate.delete(redisTemplate.keys("allUser:*"));
                log.info("clear cache all user");
            }
            else{
                authorizationDao.deleteFromAuthorization(registerRole.getUser().getId(), registerRole.getRole().getId());
                redisTemplate.delete(redisTemplate.keys("allUser:*"));
                log.info("clear cache all user");
            }
            registerRole.setAccepted(Boolean.parseBoolean(request.get("isAccepted")));
            registerRoleRepository.save(registerRole);
            return CodeSphereResponses.generateResponse(null,
                    "Role successfully change with status " + request.get("isAccepted"), HttpStatus.OK);
        }
        else
            throw new PermissionException(CodeSphereConstants.PERMISSION_DENIED);
    }
}
