package com.trungtam.LearningCenterApi.repository;

import com.trungtam.LearningCenterApi.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    List<Chapter> findByCourseIdOrderByNumberAsc(Long courseId);

    // Xóa tất cả chapters theo courseId (native by method name)
    @Transactional
    @Modifying
    void deleteByCourseId(Long courseId);

    // Kiểm tra có chương nào theo courseId
    boolean existsByCourseId(Long courseId);
}

