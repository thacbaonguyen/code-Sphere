package com.thacbao.codeSphere.entities.reference;

import com.thacbao.codeSphere.entities.core.Exercise;
import com.thacbao.codeSphere.entities.core.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

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

    @Column(name = "source_code")
    private String sourceCode;
    private String status;
    @Column(name = "pass_count")
    private int passCount;
    private int score;
    @Column(name = "total_testcase")
    private int totalTestCases;

    @OneToMany(mappedBy = "submissionHistory", cascade = CascadeType.ALL)
    private List<TestCaseHistory> testCaseHistory;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
