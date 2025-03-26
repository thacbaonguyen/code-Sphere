package com.thacbao.codeSphere.data.specification;

import com.thacbao.codeSphere.entities.core.Course;
import org.springframework.data.jpa.domain.Specification;

public class CourseSpecification {
    public static Specification<Course> hasSearchText(String searchText) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.like(root.get("title"), "%" + searchText + "%");
        };
    }
}
