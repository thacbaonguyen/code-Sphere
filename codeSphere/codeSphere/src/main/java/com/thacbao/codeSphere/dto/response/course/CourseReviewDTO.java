package com.thacbao.codeSphere.dto.response.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseReviewDTO {
    private Integer id;
    private int rating;
    private String content;
    private LocalDateTime createdAt;
    private String author;
}
