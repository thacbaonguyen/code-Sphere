package com.thacbao.codeSphere.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class UserDTO {
    private String username;

    private String fullName;

    private String email;

    private String phoneNumber;

    private String dob;

    private String createdAt;
    private String updatedAt;

    private List<String> roles;

    private String roleName;

    private String roleCode;

    public UserDTO(String username, String fullName, String email,
                   String phoneNumber, String dob, String createdAt,
                   String updatedAt, List<String> roles) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dob = dob;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.roles = roles;
    }

    public UserDTO(String username, String fullName, String roleName, String roleCode) {
        this.username = username;
        this.fullName = fullName;
        this.roleName = roleName;
        this.roleCode = roleCode;
    }
}
