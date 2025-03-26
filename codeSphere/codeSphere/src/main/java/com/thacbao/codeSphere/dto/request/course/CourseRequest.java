package com.thacbao.codeSphere.dto.request.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequest {
    @NotBlank(message = "Course title cannot be blank")
    private String title;

    @Size(min = 2, message = "Description must be lest than 2 character")
    private String description;
    private double price;

    @NotBlank(message = "Thumbnail cannot be blank")
    private String thumbnail;
    private boolean isActive = true;
    private int duration;
    private int discount;
}
