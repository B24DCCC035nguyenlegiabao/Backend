package com.trungtam.LearningCenterApi.repository;

import com.trungtam.LearningCenterApi.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // Yêu cầu: "Thống kê lịch sử học của học viên"
    // Spring tự hiểu: "findBy" + "Student" (đối tượng Student) + "Id" (trường Id của Student)
    // -> SELECT * FROM enrollments WHERE student_id = ?
    List<Enrollment> findByStudentId(Long studentId);
    // Đếm số lượng bản ghi theo trạng thái chứng chỉ
    long countByStatus(Enrollment.CertificateStatus status);

    // (Hàm này cũng có thể dùng để check xem học viên đã đăng ký khóa học đó chưa)
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
}