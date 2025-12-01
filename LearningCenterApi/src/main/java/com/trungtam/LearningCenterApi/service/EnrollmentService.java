package com.trungtam.LearningCenterApi.service;

import com.trungtam.LearningCenterApi.dto.EnrollmentDTO;
import com.trungtam.LearningCenterApi.dto.EnrollmentHistoryDTO;
import com.trungtam.LearningCenterApi.entity.Chapter;
import com.trungtam.LearningCenterApi.entity.ChapterCompletion;
import com.trungtam.LearningCenterApi.entity.Course;
import com.trungtam.LearningCenterApi.entity.Enrollment;
import com.trungtam.LearningCenterApi.entity.Student;
import com.trungtam.LearningCenterApi.entity.CertificateStatus;
import com.trungtam.LearningCenterApi.exception.ResourceNotFoundException;
import com.trungtam.LearningCenterApi.repository.ChapterCompletionRepository;
import com.trungtam.LearningCenterApi.repository.ChapterRepository;
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
    private final ChapterRepository chapterRepository;
    private final ChapterCompletionRepository chapterCompletionRepository;

    public EnrollmentDTO startCourse(Long enrollmentId) {
        // 1. Tìm bản ghi đăng ký
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));

        // 2. Kiểm tra trạng thái hiện tại (Đảm bảo chỉ có thể bắt đầu từ PENDING)
        if (enrollment.getStatus() != CertificateStatus.PENDING) {
            throw new IllegalArgumentException("Khóa học này đã bắt đầu hoặc đã hoàn thành.");
        }

        // 3. Cập nhật trạng thái thành IN_PROGRESS
        enrollment.setStatus(CertificateStatus.IN_PROGRESS);

        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);

        // 4. Trả về DTO
        return convertToDto(updatedEnrollment); // Sử dụng hàm convertToDto hiện có của bạn
    }

    // Đánh dấu một chương là đã hoàn thành cho một enrollment
    public EnrollmentDTO markChapterComplete(Long enrollmentId, Long chapterId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));

        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found with id: " + chapterId));

        // Kiểm tra chương thuộc về khóa học của enrollment
        if (!chapter.getCourse().getId().equals(enrollment.getCourse().getId())) {
            throw new IllegalArgumentException("Chương không thuộc về khóa học của enrollment này");
        }

        // Kiểm tra đã có completion chưa
        boolean already = chapterCompletionRepository.findByEnrollmentIdAndChapterId(enrollmentId, chapterId).isPresent();
        if (!already) {
            ChapterCompletion cc = new ChapterCompletion();
            cc.setEnrollment(enrollment);
            cc.setChapter(chapter);
            cc.setCompletedAt(LocalDateTime.now());
            chapterCompletionRepository.save(cc);
        }

        // Sau khi đánh dấu, kiểm tra xem số chương đã hoàn thành có bằng tổng chương của khóa học không
        long completedCount = chapterCompletionRepository.countByEnrollmentId(enrollmentId);
        long totalChapters = chapterRepository.findByCourseIdOrderByNumberAsc(enrollment.getCourse().getId()).size();

        if (totalChapters > 0 && completedCount >= totalChapters) {
            enrollment.setStatus(CertificateStatus.PENDING); // Hoàn thành tự động
            enrollmentRepository.save(enrollment);
        }

        return convertToDto(enrollment);
    }

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
        // Khi học viên mới đăng ký, trạng thái nên là PENDING (chưa bắt đầu)
        newEnrollment.setStatus(CertificateStatus.IN_PROGRESS);

        Enrollment savedEnrollment = enrollmentRepository.save(newEnrollment);

        return convertToDto(savedEnrollment);
    }

    // HÀM 2: Cấp chứng chỉ (Đã hoàn thiện)
    public EnrollmentDTO issueCertificate(Long enrollmentId, CertificateStatus status) {
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

    // Lấy tiến độ (danh sách chương + trạng thái hoàn thành) cho 1 enrollment
    public java.util.List<com.trungtam.LearningCenterApi.dto.ChapterProgressDTO> getEnrollmentProgress(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));

        Long courseId = enrollment.getCourse().getId();
        java.util.List<Chapter> chapters = chapterRepository.findByCourseIdOrderByNumberAsc(courseId);

        java.util.List<com.trungtam.LearningCenterApi.entity.ChapterCompletion> completions =
                chapterCompletionRepository.findByEnrollmentId(enrollmentId);

        java.util.Map<Long, com.trungtam.LearningCenterApi.entity.ChapterCompletion> map = new java.util.HashMap<>();
        for (com.trungtam.LearningCenterApi.entity.ChapterCompletion c : completions) {
            map.put(c.getChapter().getId(), c);
        }

        java.util.List<com.trungtam.LearningCenterApi.dto.ChapterProgressDTO> result = new java.util.ArrayList<>();
        for (Chapter ch : chapters) {
            com.trungtam.LearningCenterApi.dto.ChapterProgressDTO dto = new com.trungtam.LearningCenterApi.dto.ChapterProgressDTO();
            dto.setChapterId(ch.getId());
            dto.setNumber(ch.getNumber());
            dto.setTitle(ch.getTitle());
            com.trungtam.LearningCenterApi.entity.ChapterCompletion cc = map.get(ch.getId());
            dto.setCompleted(cc != null);
            dto.setCompletedAt(cc == null ? null : cc.getCompletedAt() == null ? null : cc.getCompletedAt().toString());
            result.add(dto);
        }

        return result;
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
    public List<EnrollmentDTO> getAllEnrollments() {
        List<Enrollment> enrollments = enrollmentRepository.findAll();
        return enrollments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Chuyển sang EnrollmentHistoryDTO (cho trang lịch sử)
    private EnrollmentHistoryDTO convertToHistoryDto(Enrollment enrollment) {
        EnrollmentHistoryDTO dto = new EnrollmentHistoryDTO();
        dto.setEnrollmentId(enrollment.getId());
        dto.setEnrollmentDate(enrollment.getEnrollmentDate());
        dto.setStatus(enrollment.getStatus());

        if (enrollment.getCourse() != null) {
            dto.setCourseCode(enrollment.getCourse().getCourseCode());
            // Điền thêm courseId vì bạn đã thêm field này vào DTO
            dto.setCourseId(enrollment.getCourse().getId());
            dto.setCourseContent(enrollment.getCourse().getContent());
            dto.setCourseStartDate(enrollment.getCourse().getStartDate());
            dto.setCourseEndDate(enrollment.getCourse().getEndDate());
        }

        return dto;
    }
}
