package com.thacbao.codeSphere.entities.reference;

import com.thacbao.codeSphere.entities.core.Exercise;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "testcases")
@Data
public class TestCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String input;

    private String output;

    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;
}
