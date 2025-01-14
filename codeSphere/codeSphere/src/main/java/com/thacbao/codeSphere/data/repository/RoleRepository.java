package com.thacbao.codeSphere.data.repository;

import com.thacbao.codeSphere.entity.reference.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
}
