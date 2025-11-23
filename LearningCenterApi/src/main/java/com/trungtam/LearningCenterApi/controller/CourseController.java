package com.trungtam.LearningCenterApi.controller;

import com.trungtam.LearningCenterApi.dto.CourseDTO;
import com.trungtam.LearningCenterApi.dto.CreateCourseRequest;
import com.trungtam.LearningCenterApi.dto.UpdateCourseRequest;
import com.trungtam.LearningCenterApi.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/courses")
// Mọi API trong Controller này đều yêu cầu người dùng phải đăng nhập (có token hợp lệ)
@PreAuthorize("isAuthenticated()")
public class CourseController {

    @Autowired
    private CourseService courseService;

    /**
     * 1. POST: Tạo mới Khóa học (Chỉ ADMIN)
     * Trả về 201 Created
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseDTO> createCourse(@Valid @RequestBody CreateCourseRequest request) {
        CourseDTO newCourse = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCourse);
    }

    /**
     * 2. GET: Lấy tất cả Khóa học (Mọi user đã đăng nhập)
     * Trả về 200 OK
     */
    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        List<CourseDTO> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    /**
     * 3. GET: Lấy Khóa học theo ID (Mọi user đã đăng nhập)
     * Trả về 200 OK
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        CourseDTO course = courseService.getCourseById(id);
        return ResponseEntity.ok(course);
    }

    /**
     * 4. PUT: Cập nhật Khóa học (Chỉ ADMIN)
     * Trả về 200 OK
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseDTO> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCourseRequest request
    ) {
        CourseDTO updatedCourse = courseService.updateCourse(id, request);
        return ResponseEntity.ok(updatedCourse);
    }

    /**
     * 5. DELETE: Xóa Khóa học (Chỉ ADMIN)
     * Trả về 204 No Content (Đã sửa lỗi 401 tại đây)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}