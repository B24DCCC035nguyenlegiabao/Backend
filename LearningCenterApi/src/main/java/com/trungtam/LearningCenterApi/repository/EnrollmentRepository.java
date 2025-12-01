package com.trungtam.LearningCenterApi.repository;

import com.trungtam.LearningCenterApi.entity.Enrollment;
import com.trungtam.LearningCenterApi.entity.CertificateStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudentId(Long studentId);
    // Đếm số lượng bản ghi theo trạng thái chứng chỉ
    long countByStatus(CertificateStatus status);

    // Method mới: Tìm enrollment theo năm đăng ký
    List<Enrollment> findByEnrollmentDateBetween(
            LocalDateTime start,
            LocalDateTime end
    );

    // Optional: Query tối ưu hơn với JOIN FETCH course (tránh lazy khi cần course info)
    @Query("SELECT e FROM Enrollment e JOIN FETCH e.course " +
            "WHERE e.enrollmentDate BETWEEN :start AND :end")
    List<Enrollment> findByEnrollmentDateBetweenWithCourse(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
    // (Hàm này cũng có thể dùng để check xem học viên đã đăng ký khóa học đó chưa)
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
}