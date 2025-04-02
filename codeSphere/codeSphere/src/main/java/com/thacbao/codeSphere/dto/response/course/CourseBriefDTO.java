package com.thacbao.codeSphere.dto.response.course;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thacbao.codeSphere.entities.core.Course;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URL;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseBriefDTO {
    private Integer id;
    private String title;
    private String description;
    private String thumbnail;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private String createdAt;
    private int duration;
    private double rating;
    private float price;
    private int sectionCount;
    private int videoCount;
    private String category;
    private URL image;

    public CourseBriefDTO(Course course, int sectionCount, int videoCount, double rating) {
        this.id = course.getId();
        this.title = course.getTitle();
        this.description = course.getDescription();
        this.thumbnail = course.getThumbnail();
        this.createdAt = course.getCreatedAt().toString();
        this.duration = course.getDuration();
        this.videoCount = videoCount;
        this.category = course.getCategory().getName();
        this.sectionCount = sectionCount;
        this.rating = rating;
        this.price = course.getPrice();
    }

    public CourseBriefDTO(Course course) {
        this.id = course.getId();
        this.title = course.getTitle();
        this.description = course.getDescription();
        this.thumbnail = course.getThumbnail();
        this.createdAt = course.getCreatedAt().toString();
        this.duration = course.getDuration();
        this.category = course.getCategory().getName();
        this.price = course.getPrice();
    }
}
