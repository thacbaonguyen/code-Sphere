package com.thacbao.codeSphere.dto.response.course;

import com.thacbao.codeSphere.entities.reference.Section;
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

    public SectionDTO(Section section, List<VideoDTO> videos) {
        this.id = section.getId();
        this.title = section.getTitle();
        this.description = section.getDescription();
        this.orderIndex = section.getOrderIndex();
        this.videos = videos;
    }
    public SectionDTO(Section section) {
        this.id = section.getId();
        this.title = section.getTitle();
        this.description = section.getDescription();
        this.orderIndex = section.getOrderIndex();
    }
}
