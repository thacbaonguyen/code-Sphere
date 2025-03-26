package com.thacbao.codeSphere.data.specification;

import com.thacbao.codeSphere.entities.core.Blog;
import com.thacbao.codeSphere.entities.core.Course;
import com.thacbao.codeSphere.entities.core.User;
import com.thacbao.codeSphere.entities.reference.CourseCategory;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

public class CourseSpecification {
    public static Specification<Course> hasSearchText(String searchText) {
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
}
