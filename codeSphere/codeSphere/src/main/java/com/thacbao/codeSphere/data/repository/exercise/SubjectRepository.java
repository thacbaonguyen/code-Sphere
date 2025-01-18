package com.thacbao.codeSphere.data.repository.exercise;

import com.thacbao.codeSphere.entities.reference.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    Subject findByName(String name);
}
