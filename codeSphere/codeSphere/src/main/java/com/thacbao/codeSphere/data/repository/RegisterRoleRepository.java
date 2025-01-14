package com.thacbao.codeSphere.data.repository;

import com.thacbao.codeSphere.entity.core.User;
import com.thacbao.codeSphere.entity.reference.RegisterRole;
import com.thacbao.codeSphere.entity.reference.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisterRoleRepository extends JpaRepository<RegisterRole, Integer> {
    Page<RegisterRole> findAll(Pageable pageable);

    RegisterRole findByUserAndRole(User user, Role role);
}
