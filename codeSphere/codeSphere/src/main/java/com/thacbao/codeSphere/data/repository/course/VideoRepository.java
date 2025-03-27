package com.thacbao.codeSphere.data.repository.course;

import com.thacbao.codeSphere.entities.reference.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Integer> {
    @Query(value = "SELECT COUNT(*) FROM videos WHERE section_id = :sectionId", nativeQuery = true)
    int countBySectionId(@Param("sectionId") int sectionId);

    @Query(value = "SELECT * FROM videos WHERE section_id = :sectionId", nativeQuery = true)
    List<Video> findBySectionId(@Param("sectionId") int sectionId);
}
