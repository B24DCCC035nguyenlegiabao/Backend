// Tạo file: com.trungtam.LearningCenterApi.service.StatisticsService.java
package com.trungtam.LearningCenterApi.service;

import com.trungtam.LearningCenterApi.dto.CourseStatsDTO;
import com.trungtam.LearningCenterApi.dto.DashboardSummaryDTO;
import com.trungtam.LearningCenterApi.entity.Course;
import com.trungtam.LearningCenterApi.entity.Enrollment;
import com.trungtam.LearningCenterApi.repository.CourseRepository;
import com.trungtam.LearningCenterApi.repository.EnrollmentRepository;
import com.trungtam.LearningCenterApi.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public CourseStatsDTO getCourseStatsByYear(int year) {
        // 1. Xác định 2 mốc thời gian
        LocalDateTime startOfYear = LocalDateTime.of(year, Month.JANUARY, 1, 0, 0);
        LocalDateTime endOfYear = LocalDateTime.of(year, Month.DECEMBER, 31, 23, 59);

        // 2. Tìm tất cả khóa học trong năm đó
        List<Course> coursesInYear = courseRepository.findByStartDateBetween(startOfYear, endOfYear);

        long totalStudents = 0;
        long totalPass = 0;
        long totalFail = 0;

        // 3. Duyệt qua từng khóa học để lấy thông tin đăng ký
        for (Course course : coursesInYear) {
            // Lấy danh sách enrollment của khóa học (đảm bảo Course entity có @OneToMany)
            Set<Enrollment> enrollments = course.getEnrollments();
            totalStudents += enrollments.size();

            totalPass += enrollments.stream()
                    .filter(e -> e.getStatus() == Enrollment.CertificateStatus.PASS)
                    .count();

            totalFail += enrollments.stream()
                    .filter(e -> e.getStatus() == Enrollment.CertificateStatus.FAIL)
                    .count();
        }

        // 4. Tạo DTO trả về
        CourseStatsDTO stats = new CourseStatsDTO();
        stats.setYear(year);
        stats.setTotalCourses(coursesInYear.size());
        stats.setTotalStudentsEnrolled(totalStudents);
        stats.setTotalPass(totalPass);
        stats.setTotalFail(totalFail);

        return stats;
    }

    /**
     * HÀM 3: Thống kê tổng quan (Fix lỗi 'getDashboardSummary')
     */
    public DashboardSummaryDTO getDashboardSummary() {
        long totalStudents = studentRepository.count();
        long totalCourses = courseRepository.count();
        long totalEnrollments = enrollmentRepository.count();
        long pendingCertificates = enrollmentRepository.countByStatus(Enrollment.CertificateStatus.PENDING);

        DashboardSummaryDTO summary = new DashboardSummaryDTO();
        summary.setTotalStudents(totalStudents);
        summary.setTotalCourses(totalCourses);
        summary.setTotalEnrollments(totalEnrollments);
        summary.setPendingCertificates(pendingCertificates);

        return summary;
    }
}