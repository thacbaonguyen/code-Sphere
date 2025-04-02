package com.thacbao.codeSphere.data.repository.course;

import com.thacbao.codeSphere.entities.reference.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Integer> {
    @Query(value = "SELECT COUNT(*) FROM sections WHERE course_id = :courseId", nativeQuery = true)
    int countByCourseId(@Param("courseId") int courseId);

    @Query(value = "SELECT * FROM sections WHERE course_id = :courseId order by order_index", nativeQuery = true)
    List<Section> findByCourseId(@Param("courseId") int courseId);
}
