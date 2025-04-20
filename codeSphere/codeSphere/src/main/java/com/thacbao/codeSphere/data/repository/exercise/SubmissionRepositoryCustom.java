package com.thacbao.codeSphere.data.repository.exercise;

import com.thacbao.codeSphere.dto.response.sandbox.SubmissionByDayResponse;

import java.util.List;

public interface SubmissionRepositoryCustom {

    List<SubmissionByDayResponse> countSubmissionsByDayForUser(Integer userId);
}
