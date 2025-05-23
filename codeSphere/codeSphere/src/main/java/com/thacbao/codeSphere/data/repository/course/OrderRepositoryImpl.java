package com.thacbao.codeSphere.data.repository.course;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.thacbao.codeSphere.configurations.CustomUserDetailsService;
import com.thacbao.codeSphere.dto.response.course.SpendByMothResponse;
import com.thacbao.codeSphere.dto.response.course.SpendResponse;
import com.thacbao.codeSphere.entities.reference.QOrder;
import com.thacbao.codeSphere.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom{

    private final CustomUserDetailsService userDetailsService;

    private final JPAQueryFactory queryFactory;

    @Override
    public List<SpendResponse> totalSpendByDay(String ago) {
        QOrder order = QOrder.order;

        LocalDate endQ = LocalDate.now();
        LocalDate startQ = endQ.minusDays(Integer.parseInt(ago));

        //trich xuat ngay
        DateTemplate<Date> dateOnly = Expressions.dateTemplate(Date.class, "DATE({0})", order.orderDate);

        List<Tuple> results = queryFactory.select(dateOnly, order.totalAmount.sum())
                .from(order)
                .where(
                        order.user.id.eq(userDetailsService.getUserDetails().getId())
                                .and(order.orderDate.between(
                                        startQ.atStartOfDay(), endQ.atStartOfDay()
                                ))
                                .and(order.paymentStatus.eq(PaymentStatus.paid))
                )
                .groupBy(dateOnly)
                .orderBy(dateOnly.asc())
                .fetch();
        return results.stream().map(tuple -> {
                    Date sqlDate = tuple.get(dateOnly);
                    LocalDate localDate = sqlDate.toLocalDate();
                    return new SpendResponse(localDate, tuple.get(order.totalAmount.sum()));
                }
                ).collect(Collectors.toList());
    }

    @Override
    public List<SpendByMothResponse> totalSpendByMonth() {
        QOrder order = QOrder.order;
        LocalDate today = LocalDate.of(LocalDate.now().getYear(), 12, 31);
        LocalDate oneYearAgo = today.minusYears(1);

        // trich xuar thang va nam
        NumberTemplate<Integer> year = Expressions.numberTemplate(Integer.class, "YEAR({0})", order.orderDate);
        NumberTemplate<Integer> month = Expressions.numberTemplate(Integer.class, "MONTH({0})", order.orderDate);

        List<Tuple> results = queryFactory.select(year, month, order.totalAmount.sum())
                .from(order)
                .where(
                        order.user.id.eq(userDetailsService.getUserDetails().getId())
                                .and(order.orderDate.between(
                                        oneYearAgo.atStartOfDay(), today.atStartOfDay()
                                ))
                                .and(order.paymentStatus.eq(PaymentStatus.paid))
                )
                .groupBy(year, month)
                .orderBy(year.asc(), month.asc())
                .fetch();
        return results.stream().map(tuple ->
                new SpendByMothResponse(tuple.get(year), tuple.get(month), tuple.get(order.totalAmount.sum()))
                ).collect(Collectors.toList());
    }

    @Override
    public List<?> totalSpendByYear(Integer userId) {
        return List.of();
    }
}
