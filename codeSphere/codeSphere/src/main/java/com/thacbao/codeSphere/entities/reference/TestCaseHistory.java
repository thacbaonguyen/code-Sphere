package com.thacbao.codeSphere.entities.reference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "testcasehistories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private boolean passed;
    @Column(name = "test_case_expected")
    private String testCaseExpected;
    private String output;

    @Column(name = "status_id")
    private int statusId;

    @Column(name = "status_name")
    private String statusName;
    private double time;
    private long memory;

    @Column(name = "error_message")
    private String errorMessage;

    @ManyToOne
    @JoinColumn(name = "submission_id")
    private SubmissionHistory submissionHistory;

}
