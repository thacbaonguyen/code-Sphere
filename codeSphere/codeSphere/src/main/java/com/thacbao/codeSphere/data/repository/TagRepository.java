package com.thacbao.codeSphere.data.repository;

import com.thacbao.codeSphere.entity.reference.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
    Tag findByName(String name);
}
