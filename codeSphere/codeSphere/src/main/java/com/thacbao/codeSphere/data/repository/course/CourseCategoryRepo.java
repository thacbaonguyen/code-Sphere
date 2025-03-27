package com.thacbao.codeSphere.data.repository.course;

import com.thacbao.codeSphere.entities.reference.CourseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseCategoryRepo extends JpaRepository<CourseCategory, Integer> {
    CourseCategory findByName(String name);
}
