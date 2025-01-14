package com.thacbao.codeSphere.dto.response.user;

import com.thacbao.codeSphere.entity.reference.RegisterRole;
import lombok.Data;

@Data
public class RegisterRoleDTO {
    private Integer id;

    private Integer roleId;

    private String roleName;

    private Boolean isAccepted;

    private UserDTO user;

    public RegisterRoleDTO(RegisterRole registerRole) {
        this.id = registerRole.getId();
        this.roleId = registerRole.getRole().getId();
        this.roleName = registerRole.getRole().getName();
        this.isAccepted = registerRole.isAccepted();
        this.user = new UserDTO(registerRole.getUser());
    }
}
