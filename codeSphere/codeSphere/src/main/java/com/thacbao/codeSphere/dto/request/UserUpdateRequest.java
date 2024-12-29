package com.thacbao.codeSphere.dto.request;

import com.thacbao.codeSphere.validations.ValidDob;
import lombok.Data;

@Data
public class UserUpdateRequest {
    private String fullName;

    private String phoneNumber;

    @ValidDob(message = "Invalid date of birth")
    private String dob;
}
