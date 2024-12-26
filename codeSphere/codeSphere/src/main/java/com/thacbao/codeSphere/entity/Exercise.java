package com.thacbao.codeSphere.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "exercises")
@Getter
@Setter
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100, unique = true)
    private String code;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String paper;

    @Column(columnDefinition = "TEXT")
    private String input;

    @Column(columnDefinition = "TEXT")
    private String output;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contribute> contributors;

    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentExercise> commentExercises;
}
