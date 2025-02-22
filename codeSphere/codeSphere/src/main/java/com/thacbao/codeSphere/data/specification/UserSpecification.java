package com.thacbao.codeSphere.data.specification;

import com.thacbao.codeSphere.entities.core.User;
import com.thacbao.codeSphere.entities.reference.Authorization;
import com.thacbao.codeSphere.entities.reference.Role;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

public class UserSpecification {
    public static Specification<User> hasSearchText(String search){
        return (root, query, criteriaBuilder) -> {
            if(search == null || search.length() == 0){
                return null;
            }
            String likePattern = "%" + search.toLowerCase() + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), likePattern),
            criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), likePattern),
            criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern),
            criteriaBuilder.like(criteriaBuilder.lower(root.get("phoneNumber")), likePattern)
            );
        };
    }

    public static Specification<User> hasNotAdmin(){
        return (root, query, criteriaBuilder) -> {
            Subquery<Long> adminRoleSubquery = query.subquery(Long.class);
            Root<User> subqueryRoot = adminRoleSubquery.from(User.class);
            Join<User, Authorization> subqueryAuthorization = subqueryRoot.join("authorizations");
            Join<Authorization, Role> subqueryRole = subqueryAuthorization.join("role");

            adminRoleSubquery.select(criteriaBuilder.count(subqueryRole))
                    .where(
                            criteriaBuilder.and(
                                    criteriaBuilder.equal(subqueryRoot.get("id"), root.get("id")),
                                    criteriaBuilder.equal(subqueryRole.get("name"), "ADMIN")
                            )
                    );
            query.distinct(true);
            return criteriaBuilder.equal(adminRoleSubquery, 0L);
        };
    }

}
