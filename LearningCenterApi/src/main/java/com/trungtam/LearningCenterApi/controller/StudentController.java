package com.trungtam.LearningCenterApi.controller;
import org.springframework.beans.factory.annotation.Autowired;
import com.trungtam.LearningCenterApi.service.StudentService;
import org.springframework.http.HttpStatus;
import com.trungtam.LearningCenterApi.dto.StudentDTO;
import com.trungtam.LearningCenterApi.dto.CreateStudentRequest;
import com.trungtam.LearningCenterApi.dto.UpdateStudentRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Phân quyền
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/students") // URL cơ sở
@PreAuthorize("isAuthenticated()") // Yêu cầu phải đăng nhập mới được dùng API này
public class StudentController {

    @Autowired
    private StudentService studentService; // Chỉ gọi Service

    /**
     * API Thêm mới học viên
     * Yêu cầu: Quyền Admin (ví dụ)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Chỉ Admin mới được thêm
    public ResponseEntity<StudentDTO> createStudent(@Valid @RequestBody CreateStudentRequest request) {
        // Controller không làm gì cả, chỉ gọi Service
        StudentDTO newStudent = studentService.createStudent(request);
        // Trả về DTO với mã 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(newStudent);
    }

    /**
     * API Lấy danh sách học viên (Hỗ trợ tìm kiếm)
     * URL: GET /api/v1/students
     * URL: GET /api/v1/students?search=Nguyen Van A
     */
    @GetMapping
    public ResponseEntity<List<StudentDTO>> searchStudents(
            @RequestParam(name = "search", required = false) String keyword
    ) {
        List<StudentDTO> students = studentService.searchStudents(keyword);
        return ResponseEntity.ok(students);
    }

    /**
     * API Lấy thông tin chi tiết 1 học viên
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        StudentDTO student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    /**
     * API Cập nhật thông tin học viên
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentDTO> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStudentRequest request
    ) {
        StudentDTO updatedStudent = studentService.updateStudent(id, request);
        return ResponseEntity.ok(updatedStudent);
    }

    /**
     * API Xóa học viên
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build(); // Trả về mã 204 No Content
    }
}