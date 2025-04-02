package com.thacbao.codeSphere.dto.request.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequest {
    @NotBlank(message = "Course title cannot be blank")
    private String title;

    @Size(min = 10, message = "Description must be lest than 10 character")
    private String description;
    private float price;

    private String isActive;
    private int duration;
    private int discount;

    private int courseCategoryId;
}
