package com.trungtam.LearningCenterApi.controller;
import org.springframework.beans.factory.annotation.Autowired;
import com.trungtam.LearningCenterApi.service.EnrollmentService;
import com.trungtam.LearningCenterApi.dto.EnrollmentDTO;
import com.trungtam.LearningCenterApi.dto.EnrollRequest;
import com.trungtam.LearningCenterApi.dto.IssueCertificateRequest;
import com.trungtam.LearningCenterApi.dto.EnrollmentHistoryDTO;
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')") // Admin hoặc nhân viên
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
}