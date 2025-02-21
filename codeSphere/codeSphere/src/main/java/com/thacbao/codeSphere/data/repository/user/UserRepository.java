package com.thacbao.codeSphere.data.repository.user;

import com.thacbao.codeSphere.entities.core.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query(value = "SELECT COUNT(*) FROM users WHERE is_active = true AND is_blocked = false", nativeQuery = true)
    long count();
}
