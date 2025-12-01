package com.trungtam.LearningCenterApi.repository;

import com.trungtam.LearningCenterApi.entity.ChapterCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterCompletionRepository extends JpaRepository<ChapterCompletion, Long> {
    List<ChapterCompletion> findByEnrollmentId(Long enrollmentId);
    Optional<ChapterCompletion> findByEnrollmentIdAndChapterId(Long enrollmentId, Long chapterId);
    long countByEnrollmentId(Long enrollmentId);
}

