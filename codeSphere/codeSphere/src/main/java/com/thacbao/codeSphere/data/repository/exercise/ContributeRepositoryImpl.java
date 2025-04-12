package com.thacbao.codeSphere.data.repository.exercise;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.thacbao.codeSphere.configurations.CustomUserDetailsService;
import com.thacbao.codeSphere.dto.response.common.CommonResponse;
import com.thacbao.codeSphere.entities.core.QContribute;
import com.thacbao.codeSphere.entities.reference.QCommentBlog;
import com.thacbao.codeSphere.entities.reference.QCommentExercise;
import com.thacbao.codeSphere.entities.reference.QSolutionStorage;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@RequiredArgsConstructor
public class ContributeRepositoryImpl implements ContributeRepositoryCustom {
    private final CustomUserDetailsService userDetailsService;
    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public List<CommonResponse> totalContribute() {
        QContribute contribute = QContribute.contribute;

        Tuple result = jpaQueryFactory.select(contribute.user.id, contribute.count())
                .from(contribute)
                .where(
                        contribute.user.id.eq(userDetailsService.getUserDetails().getId())
                )
                .groupBy(contribute.user.id)
                .fetchOne();

        Tuple contributeActive = jpaQueryFactory.select(contribute.user.id, contribute.count())
                .from(contribute)
                .where(
                        contribute.user.id.eq(userDetailsService.getUserDetails().getId())
                                .and(contribute.isActive.eq(true))
                )
                .groupBy(contribute.user.id)
                .fetchOne();
        List<CommonResponse> responses = new ArrayList<>();
        responses.add(new CommonResponse("Total contribute", result.get(contribute.count())));
        responses.add(new CommonResponse("Active contribute", contributeActive.get(contribute.count())));
        return responses;
    }

        @Override
        public CommonResponse commentWithUser() {
            QCommentBlog commentBlog = QCommentBlog.commentBlog;
            QCommentExercise commentExercise = QCommentExercise.commentExercise;
            Tuple rsBlog = jpaQueryFactory.select(commentBlog.user.id ,commentBlog.count())
                    .from(commentBlog)
                    .where(
                            commentBlog.user.id.eq(userDetailsService.getUserDetails().getId())
                    )
                    .groupBy(commentBlog.user.id)
                    .fetchOne();

            Tuple rsExercise = jpaQueryFactory.select(commentExercise.user.id ,commentExercise.count())
                    .from(commentExercise)
                    .where(
                            commentExercise.user.id.eq(userDetailsService.getUserDetails().getId())
                    )
                    .groupBy(commentExercise.user.id)
                    .fetchOne();
            Long cmtBlog = 0L;
            if (rsBlog != null) {
                cmtBlog = rsBlog.get(commentBlog.count()) != null ? rsBlog.get(commentBlog.count()) : 0L;
            }

            return new CommonResponse("Comment" ,cmtBlog + rsExercise.get(commentExercise.count()));
        }

    @Override
    public CommonResponse fileStoreWithUser() {
        QSolutionStorage solutionStorage = QSolutionStorage.solutionStorage;

        Tuple result = jpaQueryFactory.select(solutionStorage.user.id, solutionStorage.count())
                .from(solutionStorage)
                .where(
                        solutionStorage.user.id.eq(userDetailsService.getUserDetails().getId())
                )
                .groupBy(solutionStorage.user.id)
                .fetchOne();

        return new CommonResponse("Storage", result.get(solutionStorage.count()));
    }
}
