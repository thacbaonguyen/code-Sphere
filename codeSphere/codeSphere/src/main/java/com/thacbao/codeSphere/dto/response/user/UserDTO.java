package com.thacbao.codeSphere.dto.response.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thacbao.codeSphere.entities.core.User;
import com.thacbao.codeSphere.entities.reference.Authorization;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
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

    private String avatarUrl;

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

    public UserDTO(User author) {
        this.username = author.getUsername();
        this.fullName = author.getFullName();
        this.email = author.getEmail();
        this.phoneNumber = author.getPhoneNumber();
        this.dob = author.getDob().toString();
        this.createdAt = author.getCreatedAt().toString();
        this.updatedAt = author.getUpdatedAt().toString();
        this.roles = getRoles(author.getAuthorizations());
    }

    public ArrayList<String> getRoles(Set<Authorization> authorizations) {
        ArrayList<String> roles = new ArrayList<>();
        for (Authorization authorization : authorizations) {
            roles.add(authorization.getRole().getName());
        }
        return roles;
    }
}
