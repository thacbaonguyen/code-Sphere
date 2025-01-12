package com.thacbao.codeSphere.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ApiResponse {
    private LocalDateTime timestamp;
    private int code;
    private String status;
    private String message;
    private Object data;

}
