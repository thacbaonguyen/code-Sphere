package com.thacbao.codeSphere.dto.request.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectionRequest {
    private String title;
    private String description;
    private int orderIndex;
    private int courseId;
}
