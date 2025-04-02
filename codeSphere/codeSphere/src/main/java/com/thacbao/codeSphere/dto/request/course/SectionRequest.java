package com.thacbao.codeSphere.dto.request.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectionRequest {
    @NotBlank(message = "Title cannot be empty")
    private String title;
    private String description;

    @NotNull(message = "Order index cannot be null")
    private int orderIndex;

    @NotNull(message = "Course cannot be empty")
    private int courseId;
}
