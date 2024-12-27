package com.thacbao.codeSphere.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String username;

    private String fullName;

    private String email;

    private String phoneNumber;

    private LocalDate dob;

    private LocalDate createdAt;
    private LocalDate updatedAt;

    private String roleName;

    private String roleCode;

    public UserDTO(String username, String fullName, String roleName, String roleCode) {
        this.username = username;
        this.fullName = fullName;
        this.roleName = roleName;
        this.roleCode = roleCode;
    }
}
