package com.thacbao.codeSphere.data.repository.course;

import com.thacbao.codeSphere.dto.response.course.SpendByMothResponse;
import com.thacbao.codeSphere.dto.response.course.SpendResponse;

import java.util.List;

public interface OrderRepositoryCustom {

    List<SpendResponse> totalSpendByDay(String ago);

    List<SpendByMothResponse> totalSpendByMonth();
    List<?> totalSpendByYear(Integer userId);
}
