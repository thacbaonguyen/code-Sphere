package com.thacbao.codeSphere.data.repository.user;

import com.thacbao.codeSphere.entities.core.User;
import com.thacbao.codeSphere.entities.reference.RegisterRole;
import com.thacbao.codeSphere.entities.reference.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisterRoleRepository extends JpaRepository<RegisterRole, Integer> {
    Page<RegisterRole> findAll(Pageable pageable);

    RegisterRole findByUserAndRole(User user, Role role);
}
