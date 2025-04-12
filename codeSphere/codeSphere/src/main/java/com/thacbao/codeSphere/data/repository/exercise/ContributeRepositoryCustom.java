package com.thacbao.codeSphere.data.repository.exercise;

import com.thacbao.codeSphere.dto.response.common.CommonResponse;

import java.util.List;

public interface ContributeRepositoryCustom {
    List<CommonResponse> totalContribute();

    CommonResponse commentWithUser();

    CommonResponse fileStoreWithUser();
}
