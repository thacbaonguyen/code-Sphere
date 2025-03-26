package com.thacbao.codeSphere.dto.response.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectionDTO {
    private Integer id;
    private String title;
    private String description;
    private int orderIndex;
    private List<VideoDTO> videos;
}
