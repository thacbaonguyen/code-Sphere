package com.thacbao.codeSphere.entity.reference;

import com.thacbao.codeSphere.entity.core.Exercise;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "subjects")
@Data
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Exercise> exercises;
}
