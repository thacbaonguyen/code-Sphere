package com.thacbao.codeSphere.data.repository.user;

import com.thacbao.codeSphere.entities.reference.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
}
