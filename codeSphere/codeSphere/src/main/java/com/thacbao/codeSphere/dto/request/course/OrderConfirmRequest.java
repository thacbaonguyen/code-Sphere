package com.thacbao.codeSphere.dto.request.course;

import lombok.Data;

@Data
public class OrderConfirmRequest {
    private String status;
    private String orderCode;
}
