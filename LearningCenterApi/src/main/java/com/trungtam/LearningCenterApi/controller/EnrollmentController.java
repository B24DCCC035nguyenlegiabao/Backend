package com.trungtam.LearningCenterApi.controller;
import org.springframework.beans.factory.annotation.Autowired;
import com.trungtam.LearningCenterApi.service.EnrollmentService;
import com.trungtam.LearningCenterApi.dto.EnrollmentDTO;
import com.trungtam.LearningCenterApi.dto.EnrollRequest;
import com.trungtam.LearningCenterApi.dto.IssueCertificateRequest;
import com.trungtam.LearningCenterApi.dto.EnrollmentHistoryDTO;
import com.trungtam.LearningCenterApi.dto.ChapterCompletionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1") // Dùng URL cơ sở
@PreAuthorize("isAuthenticated()")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    /**
     * API Đăng ký học viên vào khóa học
     */
    @PostMapping("/enrollments")
    public ResponseEntity<EnrollmentDTO> enrollStudentToCourse(
            @Valid @RequestBody EnrollRequest request
    ) {
        EnrollmentDTO newEnrollment = enrollmentService.enrollStudent(request.getStudentId(), request.getCourseId());
        return ResponseEntity.ok(newEnrollment);
    }

    /**
     * API Cấp chứng chỉ cho một lần đăng ký
     */
    @PutMapping("/enrollments/{enrollmentId}/certificate")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ ADMIN (gộp STAFF vào ADMIN theo phương án B)
    public ResponseEntity<EnrollmentDTO> issueCertificate(
            @PathVariable Long enrollmentId,
            @Valid @RequestBody IssueCertificateRequest request
    ) {
        EnrollmentDTO updatedEnrollment = enrollmentService.issueCertificate(enrollmentId, request.getStatus());
        return ResponseEntity.ok(updatedEnrollment);
    }

    /**
     * API Lấy lịch sử học của 1 học viên
     */
    @GetMapping("/students/{studentId}/history")
    public ResponseEntity<List<EnrollmentHistoryDTO>> getStudentHistory(
            @PathVariable Long studentId
    ) {
        List<EnrollmentHistoryDTO> history = enrollmentService.getStudentHistory(studentId);
        return ResponseEntity.ok(history);
    }
    @PutMapping("/enrollments/{enrollmentId}/start")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ ADMIN mới được bắt đầu khóa học (gộp STAFF -> ADMIN)
    public ResponseEntity<EnrollmentDTO> startEnrollment(
            @PathVariable Long enrollmentId
    ) {
        EnrollmentDTO updatedEnrollment = enrollmentService.startCourse(enrollmentId);
        return ResponseEntity.ok(updatedEnrollment);
    }

    // Người dùng đánh dấu hoàn thành 1 chương (user)
    @PostMapping("/enrollments/complete-chapter")
    public ResponseEntity<EnrollmentDTO> completeChapter(@Valid @RequestBody ChapterCompletionRequest request) {
        EnrollmentDTO dto = enrollmentService.markChapterComplete(request.getEnrollmentId(), request.getChapterId());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/enrollments/{enrollmentId}/progress")
    public ResponseEntity<java.util.List<com.trungtam.LearningCenterApi.dto.ChapterProgressDTO>> getEnrollmentProgress(
            @PathVariable Long enrollmentId
    ) {
        java.util.List<com.trungtam.LearningCenterApi.dto.ChapterProgressDTO> progress = enrollmentService.getEnrollmentProgress(enrollmentId);
        return ResponseEntity.ok(progress);
    }
    @GetMapping("/enrollments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EnrollmentDTO>> getAllEnrollments() {
        List<EnrollmentDTO> enrollments = enrollmentService.getAllEnrollments();
        return ResponseEntity.ok(enrollments);
    }
}