package com.thacbao.codeSphere.dto.response.course;

import com.thacbao.codeSphere.entities.core.Course;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private Integer id;
    private String title;
    private String description;
    private String thumbnail;
    private LocalDate createdAt;
    private int duration;
    private double rating;
    private int sectionCount;
    private int videoCount;
    private String category;
    private URL image;

    private List<CourseReviewDTO> courseReviews;
    private List<SectionDTO> sections;

    public CourseDTO(Course course, List<CourseReviewDTO> courseReviews, List<SectionDTO> sections, int rating) {
        this.id = course.getId();
        this.title = course.getTitle();
        this.description = course.getDescription();
        this.thumbnail = course.getThumbnail();
        this.createdAt = course.getCreatedAt();
        this.duration = course.getDuration();
        this.category = course.getCategory().getName();
        this.rating = rating;
        this.videoCount = videoCount(sections);
        this.sectionCount = sections.size();
        this.courseReviews = courseReviews == null ? new ArrayList<>() : courseReviews;
        this.sections = sections;
    }
    private int videoCount(List<SectionDTO> sections) {
        int result = 0;
        for (SectionDTO sectionDTO : sections) {
            result += sectionDTO.getVideos().size();
        }
        return result;
    }
}
