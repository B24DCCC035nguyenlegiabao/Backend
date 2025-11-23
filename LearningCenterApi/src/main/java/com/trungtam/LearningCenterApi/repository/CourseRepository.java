package com.trungtam.LearningCenterApi.repository;

import com.trungtam.LearningCenterApi.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Yêu cầu: "Thống kê tình hình mở khóa học theo năm (tính theo ngày bắt đầu)"
    // Spring tự hiểu: "findBy" + "StartDate" + "Between"
    // -> SELECT * FROM courses WHERE start_date BETWEEN ? AND ?
    List<Course> findByStartDateBetween(LocalDateTime startOfYear, LocalDateTime endOfYear);

    // (Service sẽ có nhiệm vụ tính toán startOfYear và endOfYear để truyền vào đây)
}