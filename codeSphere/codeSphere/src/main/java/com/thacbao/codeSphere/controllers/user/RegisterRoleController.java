package com.thacbao.codeSphere.controllers.user;

import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.exceptions.user.PermissionException;
import com.thacbao.codeSphere.services.userImpl.RegisterRoleService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLDataException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/register-role")
@RequiredArgsConstructor
public class RegisterRoleController {

    private final RegisterRoleService registerService;

    @GetMapping("/all-roles")
    public ResponseEntity<ApiResponse> getAllRoles() {
        return registerService.getAllRoles();
    }

    @PostMapping("/send-request")
    public ResponseEntity<ApiResponse> sendRequestRegisterRole(@RequestBody Map<String, String> request){
        return registerService.sendRequestRegisterRole(request);
    }

    @GetMapping("/all-request")
    public ResponseEntity<ApiResponse> getAllRequestRegisterRole(@RequestParam(required = false) String search,
                                                                 @RequestParam(defaultValue = "1") Integer page,
                                                                 @RequestParam(required = false) String role,
                                                                 @RequestParam(required = false) String status){
        return registerService.getAllRequestRegisterRole(search, page, role, status);
    }

    @PutMapping("/activate-role-for-user/{id}")
    public ResponseEntity<ApiResponse> activateRoleForUser(@PathVariable("id") Integer id,
                                                           @RequestBody Map<String, String> request){
        try{
            return registerService.activateRoleForUser(id, request);
        }
        catch(SQLDataException ex){
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (PermissionException ex){
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.FORBIDDEN);
        }
    }
}
