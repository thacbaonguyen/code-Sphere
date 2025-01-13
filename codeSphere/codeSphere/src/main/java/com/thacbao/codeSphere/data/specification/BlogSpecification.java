package com.thacbao.codeSphere.data.specification;

import com.thacbao.codeSphere.entity.core.Blog;
import com.thacbao.codeSphere.entity.reference.Tag;
import com.thacbao.codeSphere.enums.BlogStatus;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

public class BlogSpecification {

    public static Specification<Blog> hasSearchText(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.isEmpty()) return null;
            String likePattern = "%" + search.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("excerpt")), likePattern)
            );
        };
    }

    public static Specification<Blog> hasIsFeatured(String isFeatured) {
        return (root, query, criteriaBuilder) -> {
            if (isFeatured == null) return null;
            return criteriaBuilder.equal(root.get("isFeatured"), Boolean.valueOf(isFeatured));
        };
    }

    public static Specification<Blog> hasStatus() {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("status"), BlogStatus.published);
        };
    }

    public static Specification<Blog> hasTag(String tag) {
        return (root, query, criteriaBuilder) -> {
            Join<Blog, Tag> tagJoin = root.join("tags", JoinType.INNER);
            return criteriaBuilder.equal(tagJoin.get("name"), tag);
        };
    }
}
