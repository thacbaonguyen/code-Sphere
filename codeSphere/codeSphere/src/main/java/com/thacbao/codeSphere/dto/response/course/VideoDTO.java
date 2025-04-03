package com.thacbao.codeSphere.dto.response.course;

import com.thacbao.codeSphere.entities.reference.Video;
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

    public VideoDTO(Video video) {
        this.id = video.getId();
        this.title = video.getTitle();
        this.orderIndex = video.getOrderIndex();
    }
}
