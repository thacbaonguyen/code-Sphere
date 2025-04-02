package com.thacbao.codeSphere.dto.request.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentLinkRequestBody {
    private String productName;
    private String description;
    private String returnUrl;
    private String cancelUrl;
}
