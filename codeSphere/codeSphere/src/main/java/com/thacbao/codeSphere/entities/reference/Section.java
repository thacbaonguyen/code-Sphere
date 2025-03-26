package com.thacbao.codeSphere.entities.reference;

import com.thacbao.codeSphere.entities.core.Course;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "sections")
@Data
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String description;

    @Column(name = "order_index")
    private int orderIndex;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToMany(mappedBy = "section")
    private List<Video> videos;
}
