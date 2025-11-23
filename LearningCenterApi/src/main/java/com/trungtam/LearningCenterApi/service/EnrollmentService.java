package com.trungtam.LearningCenterApi.service;

import com.trungtam.LearningCenterApi.dto.EnrollmentDTO;
import com.trungtam.LearningCenterApi.dto.EnrollmentHistoryDTO;
import com.trungtam.LearningCenterApi.entity.Course;
import com.trungtam.LearningCenterApi.entity.Enrollment;
import com.trungtam.LearningCenterApi.entity.Student;
import com.trungtam.LearningCenterApi.exception.ResourceNotFoundException;
import com.trungtam.LearningCenterApi.repository.CourseRepository;
import com.trungtam.LearningCenterApi.repository.EnrollmentRepository;
import com.trungtam.LearningCenterApi.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    // HÀM 1: Đăng ký học
    public EnrollmentDTO enrollStudent(Long studentId, Long courseId) {
        // 1. Tìm Student
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        // 2. Tìm Course
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        // 3. Tạo bản ghi Enrollment mới
        Enrollment newEnrollment = new Enrollment();
        newEnrollment.setStudent(student);
        newEnrollment.setCourse(course);
        newEnrollment.setEnrollmentDate(LocalDateTime.now());
        newEnrollment.setStatus(Enrollment.CertificateStatus.PENDING);

        Enrollment savedEnrollment = enrollmentRepository.save(newEnrollment);

        return convertToDto(savedEnrollment);
    }

    // HÀM 2: Cấp chứng chỉ (Đã hoàn thiện)
    public EnrollmentDTO issueCertificate(Long enrollmentId, Enrollment.CertificateStatus status) {
        // 1. Tìm bản ghi đăng ký
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));

        // 2. Cập nhật trạng thái
        enrollment.setStatus(status);

        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);

        return convertToDto(updatedEnrollment);
    }

    // HÀM 3: Lấy lịch sử học
    public List<EnrollmentHistoryDTO> getStudentHistory(Long studentId) {
        // 1. Kiểm tra học viên tồn tại
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }

        // 2. Lấy tất cả đăng ký của học viên đó
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);

        // 3. Chuyển đổi sang DTO Lịch sử
        return enrollments.stream()
                .map(this::convertToHistoryDto)
                .collect(Collectors.toList());
    }


    // --- HÀM HỖ TRỢ CHUYỂN ĐỔI (MAPPER) ---

    // Chuyển sang EnrollmentDTO (trả về chung)
    private EnrollmentDTO convertToDto(Enrollment enrollment) {
        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setId(enrollment.getId());
        dto.setEnrollmentDate(enrollment.getEnrollmentDate());
        dto.setStatus(enrollment.getStatus());

        // Lấy thông tin từ student (đã sửa để dùng ho và ten)
        if (enrollment.getStudent() != null) {
            dto.setStudentId(enrollment.getStudent().getId());
            dto.setStudentFullName(enrollment.getStudent().getHo() + " " + enrollment.getStudent().getTen());
        }

        // Lấy thông tin từ course
        if (enrollment.getCourse() != null) {
            dto.setCourseId(enrollment.getCourse().getId());
            dto.setCourseCode(enrollment.getCourse().getCourseCode());
        }

        return dto;
    }

    // Chuyển sang EnrollmentHistoryDTO (cho trang lịch sử)
    private EnrollmentHistoryDTO convertToHistoryDto(Enrollment enrollment) {
        EnrollmentHistoryDTO dto = new EnrollmentHistoryDTO();
        dto.setEnrollmentId(enrollment.getId());
        dto.setEnrollmentDate(enrollment.getEnrollmentDate());
        dto.setStatus(enrollment.getStatus());

        if (enrollment.getCourse() != null) {
            dto.setCourseCode(enrollment.getCourse().getCourseCode());
            dto.setCourseContent(enrollment.getCourse().getContent());
            dto.setCourseStartDate(enrollment.getCourse().getStartDate());
            dto.setCourseEndDate(enrollment.getCourse().getEndDate());
        }

        return dto;
    }
}