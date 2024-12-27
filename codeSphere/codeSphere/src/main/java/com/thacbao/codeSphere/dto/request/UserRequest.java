package com.thacbao.codeSphere.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thacbao.codeSphere.validations.ValidDob;
import com.thacbao.codeSphere.validations.ValidEmail;
import com.thacbao.codeSphere.validations.ValidUsername;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @ValidUsername(message = "Username must only contain letters, numbers, and underscores")
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

    @NotNull(message = "Date of birth cannot be blank")
    @ValidDob(message = "User must be at least 12 year old")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dob;

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(
            regexp = "^[0-9]{10,15}$", message = "Phone number must be numeric and between 10 and 15 digits"
    )
    private String phoneNumber;

    @NotBlank(message = "Email cannot be blank")
    @ValidEmail(message = "Email does not follow a valid format")
    private String email;
}
