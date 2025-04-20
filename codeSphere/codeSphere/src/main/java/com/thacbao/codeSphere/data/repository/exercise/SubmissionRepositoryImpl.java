package com.thacbao.codeSphere.data.repository.exercise;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.thacbao.codeSphere.dto.response.sandbox.SubmissionByDayResponse;
import com.thacbao.codeSphere.entities.reference.QSubmissionHistory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class SubmissionRepositoryImpl implements SubmissionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<SubmissionByDayResponse> countSubmissionsByDayForUser(Integer userId) {
        QSubmissionHistory submission = QSubmissionHistory.submissionHistory;

        LocalDate today = LocalDate.now();
        LocalDate oneYearAgo = today.minusYears(1);
        List<Tuple> result = queryFactory.select(submission.createdAt, submission.count())
                .from(submission)
                .where(
                        submission.user.id.eq(userId)
                                .and(submission.createdAt.between(
                                         oneYearAgo,
                                        today)
                                )
                )
                .groupBy(submission.createdAt)
                .orderBy(submission.createdAt.asc())
                .fetch();
        return result.stream()
                .map(tuple -> new SubmissionByDayResponse(
                        tuple.get(submission.createdAt),
                        tuple.get(submission.count())
                ))
                .collect(Collectors.toList());
    }
}
