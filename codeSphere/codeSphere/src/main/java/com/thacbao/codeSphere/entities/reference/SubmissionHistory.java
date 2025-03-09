package com.thacbao.codeSphere.entities.reference;

import com.thacbao.codeSphere.entities.core.Exercise;
import com.thacbao.codeSphere.entities.core.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "submissionhistories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String status;
    @Column(name = "pass_count")
    private int passCount;
    private int score;
    @Column(name = "total_testcase")
    private int totalTestCases;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
