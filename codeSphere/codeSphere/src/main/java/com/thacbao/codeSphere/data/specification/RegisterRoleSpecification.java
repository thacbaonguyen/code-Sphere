package com.thacbao.codeSphere.data.specification;

import com.thacbao.codeSphere.entities.core.User;
import com.thacbao.codeSphere.entities.reference.RegisterRole;
import com.thacbao.codeSphere.entities.reference.Role;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

public class RegisterRoleSpecification {
    public static Specification<RegisterRole> hasStatus(String status) {
        if (status == null || status.isEmpty()) return null;
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("isAccepted"), Boolean.parseBoolean(status));
        };
    }

    public static Specification<RegisterRole> hasUserName(String userName) {
        if (userName == null || userName.isEmpty()) return null;
        String likePattern = "%" + userName.toLowerCase() + "%";
        return (root, query, criteriaBuilder) -> {
            Join<RegisterRole, User> userJoin = root.join("user", JoinType.INNER);
            return criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("username")), likePattern);
        };
    }

    public static Specification<RegisterRole> hasRole(String role) {
        if (role == null || role.isEmpty()) return null;
        String likePattern = "%" + role.toLowerCase() + "%";
        return (root, query, criteriaBuilder) -> {
            Join<RegisterRole, Role> roleJoin = root.join("role", JoinType.INNER);
            return criteriaBuilder.like(criteriaBuilder.lower(roleJoin.get("name")), likePattern);
        };
    }
}
