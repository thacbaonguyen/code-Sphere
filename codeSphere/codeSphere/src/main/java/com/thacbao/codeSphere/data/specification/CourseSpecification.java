package com.thacbao.codeSphere.data.specification;

import com.thacbao.codeSphere.entities.core.Blog;
import com.thacbao.codeSphere.entities.core.Course;
import com.thacbao.codeSphere.entities.core.User;
import com.thacbao.codeSphere.entities.reference.CourseCategory;
import com.thacbao.codeSphere.entities.reference.Order;
import com.thacbao.codeSphere.entities.reference.OrderDetail;
import com.thacbao.codeSphere.enums.PaymentStatus;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

public class CourseSpecification {
    public static Specification<Course> hasSearchText(String searchText) {
        if (searchText == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.like(root.get("title"), "%" + searchText + "%");
        };
    }
    public static Specification<Course> hasCategory(Integer categoryId) {
        return (root, query, criteriaBuilder) -> {
            Join<Course, CourseCategory> categoryJoin = root.join("category", JoinType.INNER);
            return criteriaBuilder.equal(categoryJoin.get("id"), categoryId);
        };
    }
    public static Specification<Course> hasPaid(String username) {
        return (root, query, criteriaBuilder) -> {
            Join<Course, OrderDetail> orderDetailJoin = root.join("orderDetail", JoinType.INNER);
            Join<OrderDetail, Order> orderJoin = orderDetailJoin.join("order", JoinType.INNER);
            Join<Order, User> userJoin = orderJoin.join("user", JoinType.INNER);
            Predicate paymentStatusPredicate = criteriaBuilder.equal(
                    orderJoin.get("paymentStatus"), PaymentStatus.paid);
            Predicate userIdPredicate = criteriaBuilder.equal(
                    userJoin.get("username"), username);
            return criteriaBuilder.and(paymentStatusPredicate, userIdPredicate);
        };
    }
}
