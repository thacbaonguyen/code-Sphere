package com.thacbao.codeSphere.data.repository.exercise;

import com.querydsl.core.Tuple;
import com.thacbao.codeSphere.dto.response.judge0.SubmissionByDayResponse;

import java.util.List;

public interface SubmissionRepositoryCustom {

    List<SubmissionByDayResponse> countSubmissionsByDayForUser(Integer userId);
}
