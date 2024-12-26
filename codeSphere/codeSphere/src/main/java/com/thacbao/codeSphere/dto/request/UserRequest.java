package com.thacbao.codeSphere.dto.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class UserRequest {
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String password;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String retypePassword;

    @NotBlank(message = "Full name cannot be blank")
    @Size(min = 3, max = 100, message = "Full name must be between 3 and 100 characters")
    private String fullName;

    @NotBlank(message = "Date of birth cannot be blank")
    private LocalDate dob;

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(
            regexp = "^[0-9]{10,15}$", message = "Phone number must be numeric and between 10 and 15 digits"
    )
    private String phoneNumber;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    private String email;
}
