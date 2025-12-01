// Tạo file: com.trungtam.LearningCenterApi.service.StatisticsService.java
package com.trungtam.LearningCenterApi.service;

import com.trungtam.LearningCenterApi.dto.CourseStatsDTO;
import com.trungtam.LearningCenterApi.dto.DashboardSummaryDTO;
import com.trungtam.LearningCenterApi.entity.Course;
import com.trungtam.LearningCenterApi.entity.Enrollment;
import com.trungtam.LearningCenterApi.entity.CertificateStatus;
import com.trungtam.LearningCenterApi.repository.CourseRepository;
import com.trungtam.LearningCenterApi.repository.EnrollmentRepository;
import com.trungtam.LearningCenterApi.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    /**
     * HÀM 1: Thống kê học viên theo tỉnh (Fix lỗi 'countStudentsByProvince')
     */
    public Map<String, Long> countStudentsByProvince() {
        List<Object[]> results = studentRepository.countStudentsByProvince();

        // Chuyển List<Object[]> (ví dụ: ["Hà Nội", 15]) thành Map<String, Long>
        return results.stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0], // Key: Tên tỉnh
                        arr -> (Long) arr[1]    // Value: Số lượng
                ));
    }

    /**
     * HÀM 2: Thống kê khóa học theo năm (Fix lỗi 'getCourseStatsByYear')
     */
    @Transactional(readOnly = true)
    public CourseStatsDTO getCourseStatsByYear(int year) {
        // 1. Xác định 2 mốc thời gian
        LocalDateTime startOfYear = LocalDateTime.of(year, Month.JANUARY, 1, 0, 0);
        LocalDateTime endOfYear   = LocalDateTime.of(year, Month.DECEMBER, 31, 23, 59, 59);

        // Lấy TẤT CẢ enrollments trong năm theo enrollmentDate
        List<Enrollment> enrollmentsInYear =
                enrollmentRepository.findByEnrollmentDateBetween(startOfYear, endOfYear);

        // Tính toán trực tiếp
        long totalStudents = enrollmentsInYear.size();

        long totalPass = enrollmentsInYear.stream()
                .filter(e -> e.getStatus() == CertificateStatus.PASS)
                .count();

        long totalFail = enrollmentsInYear.stream()
                .filter(e -> e.getStatus() == CertificateStatus.FAIL)
                .count();
        long totalInProgress = enrollmentsInYear.stream()
                .filter(e -> e.getStatus() == CertificateStatus.IN_PROGRESS)
                .count();
        long totalPending = enrollmentsInYear.stream()
                .filter(e -> e.getStatus() == CertificateStatus.PENDING)
                .count();

        // Đếm số khóa học duy nhất có enrollment trong năm
        long totalCourses = enrollmentsInYear.stream()
                .map(e -> e.getCourse().getId()) // Cần FETCH course nếu dùng JOIN FETCH ở trên
                .distinct()
                .count();

        return new CourseStatsDTO(year, totalCourses, totalStudents,
                totalPending, totalInProgress, totalPass, totalFail);
    }

    /**
     * HÀM 3: Thống kê tổng quan (Fix lỗi 'getDashboardSummary')
     */
    public DashboardSummaryDTO getDashboardSummary() {
        long totalStudents = studentRepository.count();
        long totalCourses = courseRepository.count();
        long totalEnrollments = enrollmentRepository.count();
        long pendingCertificates = enrollmentRepository.countByStatus(CertificateStatus.PENDING);

        DashboardSummaryDTO summary = new DashboardSummaryDTO();
        summary.setTotalStudents(totalStudents);
        summary.setTotalCourses(totalCourses);
        summary.setTotalEnrollments(totalEnrollments);
        summary.setPendingCertificates(pendingCertificates);

        return summary;
    }
}