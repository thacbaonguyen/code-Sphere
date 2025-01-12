package com.thacbao.codeSphere.data.repository;

import com.thacbao.codeSphere.entity.reference.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    Subject findByName(String name);
}
