package com.thacbao.codeSphere.entities.reference;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "coursecategories")
@Data
public class CourseCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;
    private String description;
}
