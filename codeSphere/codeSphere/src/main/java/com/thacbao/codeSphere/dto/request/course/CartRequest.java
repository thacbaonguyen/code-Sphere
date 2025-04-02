package com.thacbao.codeSphere.dto.request.course;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CartRequest {

    @NotNull(message = "Course cannot be empty")
    private Integer courseId;
}
