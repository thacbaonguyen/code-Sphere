package com.thacbao.codeSphere.dto.response.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoDTO {
    private Integer id;
    private String title;
    private String videoUrl;
    private int orderIndex;
    private String s3url;
    private String createdAt;
}
