package com.thacbao.codeSphere.entities.reference;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "videos")
@Getter
@Setter
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "video_url", nullable = false, length = 255)
    private String videoUrl;

    @Column(name = "order_index")
    private Integer orderIndex;

    private String s3url;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;
}
