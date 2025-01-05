package com.thacbao.codeSphere.repositories;

import com.thacbao.codeSphere.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    Subject findByName(String name);
}
