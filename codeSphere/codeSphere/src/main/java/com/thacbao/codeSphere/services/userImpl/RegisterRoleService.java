package com.thacbao.codeSphere.services.userImpl;

import com.thacbao.codeSphere.configurations.CustomUserDetailsService;
import com.thacbao.codeSphere.configurations.JwtFilter;
import com.thacbao.codeSphere.constants.CodeSphereConstants;
import com.thacbao.codeSphere.data.dao.AuthorizationDao;
import com.thacbao.codeSphere.data.repository.user.RegisterRoleRepository;
import com.thacbao.codeSphere.data.repository.user.RoleRepository;
import com.thacbao.codeSphere.data.repository.user.UserRepository;
import com.thacbao.codeSphere.data.specification.RegisterRoleSpecification;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.user.RegisterRoleDTO;
import com.thacbao.codeSphere.dto.response.user.UserDTO;
import com.thacbao.codeSphere.entities.core.User;
import com.thacbao.codeSphere.entities.reference.Authorization;
import com.thacbao.codeSphere.entities.reference.RegisterRole;
import com.thacbao.codeSphere.entities.reference.Role;
import com.thacbao.codeSphere.exceptions.common.AlreadyException;
import com.thacbao.codeSphere.exceptions.common.AppException;
import com.thacbao.codeSphere.exceptions.common.NotFoundException;
import com.thacbao.codeSphere.exceptions.user.PermissionException;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterRoleService {

    private final JwtFilter jwtFilter;
    private final RegisterRoleRepository registerRoleRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthorizationDao authorizationDao;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CustomUserDetailsService userDetailsService;

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
                    () -> new NotFoundException("Cannot found this user")
            );
            UserDTO userDTO = new UserDTO(user);
            Role role = roleRepository.findById(Integer.parseInt(request.get("roleId")))
                    .orElseThrow(() -> new NotFoundException("Role not found"));
            if (role.getName().equalsIgnoreCase("admin")){
                throw new PermissionException("You are not allowed to register admin role");
            }

            if (userDTO.getRoles().contains(role.getName())){
                throw new AlreadyException("You already have this role");
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

    public ResponseEntity<ApiResponse> getAllRoles(){
        List<Role> roles = roleRepository.findAll();
        Map<String, Object> customResponse = new HashMap<>();

        roles.stream().forEach(role -> {
            customResponse.put("roleName", role.getName());
            customResponse.put("roleId", role.getId());
        });

        return CodeSphereResponses.generateResponse(customResponse, "All roles", HttpStatus.OK);
    }

    /**
     * Lấy ra tất cả các request, bao gồm id, role_id, user_id, status -> update api
     * @return
     */
    public ResponseEntity<ApiResponse> getAllRequestRegisterRole(String search, Integer page, String role, String status){
        if (jwtFilter.isAdmin()){
            Pageable pageable = PageRequest.of(page - 1, 20);
            Specification<RegisterRole> spec = Specification.where(RegisterRoleSpecification.hasUserName(search))
                    .and(RegisterRoleSpecification.hasRole(role))
                    .and(RegisterRoleSpecification.hasStatus(status));
            Page<RegisterRole> registerRoles = registerRoleRepository.findAll(spec, pageable);
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
                () -> new NotFoundException("register not found")
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
