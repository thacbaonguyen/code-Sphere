package com.thacbao.codeSphere.data.repository.course;

import com.thacbao.codeSphere.dto.response.course.SpendByMothResponse;
import com.thacbao.codeSphere.dto.response.course.SpendResponse;

import java.util.List;

public interface OrderRepositoryCustom {

    List<SpendResponse> totalSpendByDay(Integer userId);

    List<SpendByMothResponse> totalSpendByMonth(Integer userId);
    List<?> totalSpendByYear(Integer userId);
}
