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
import java.util.ArrayList;
import java.util.List;

public class CourseSpecification {
    public static Specification<Course> hasSearchText(String searchText) {
        if (searchText == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.like(root.get("title"), "%" + searchText + "%");
        };
    }

    public static Specification<Course> hasRating(Float rating) {
        if (rating == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.greaterThanOrEqualTo(root.get("rate"), rating);
        };
    }

    public static Specification<Course> hasDuration(List<String> durations) {
        if (durations == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            for (String duration : durations) {
                switch (duration.toLowerCase()) {
                    case "extrashort":
                        predicates.add(criteriaBuilder.between(root.get("duration"), 0, 5));
                        break;
                    case "short":
                        predicates.add(criteriaBuilder.between(root.get("duration"), 5, 10));
                        break;
                    case "medium":
                        predicates.add(criteriaBuilder.between(root.get("duration"), 10, 15));
                        break;
                    case "long":
                        predicates.add(criteriaBuilder.between(root.get("duration"), 15, 25));
                        break;
                    case "extralong":
                        predicates.add(criteriaBuilder.between(root.get("duration"), 25, Integer.MAX_VALUE));
                        break;
                    default:
                        break;
                }
            }
            if (predicates.isEmpty()) {
                return null;
            }
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Course> hasPrice(Boolean isFree) {
        if (isFree == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            if (isFree) {
                return criteriaBuilder.equal(root.get("price"), 0);
            } else {
                return criteriaBuilder.greaterThan(root.get("price"), 0);
            }
        };
    }

    public static Specification<Course> hasCategory(Integer categoryId) {
        if (categoryId == null) {
            return null;
        }
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
