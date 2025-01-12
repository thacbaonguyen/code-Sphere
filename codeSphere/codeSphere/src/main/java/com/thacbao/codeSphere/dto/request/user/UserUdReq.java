package com.thacbao.codeSphere.dto.request.user;

import com.thacbao.codeSphere.validations.ValidDob;
import lombok.Data;

@Data
public class UserUdReq {
    private String fullName;

    private String phoneNumber;

    @ValidDob(message = "Invalid date of birth")
    private String dob;
}
