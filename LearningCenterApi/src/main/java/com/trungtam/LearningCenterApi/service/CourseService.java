package com.trungtam.LearningCenterApi.service;

import com.trungtam.LearningCenterApi.dto.CourseDTO;
import com.trungtam.LearningCenterApi.dto.CreateCourseRequest;
import com.trungtam.LearningCenterApi.dto.UpdateCourseRequest;
import com.trungtam.LearningCenterApi.entity.Course;
import com.trungtam.LearningCenterApi.exception.ResourceNotFoundException;
import com.trungtam.LearningCenterApi.repository.CourseRepository;
import com.trungtam.LearningCenterApi.repository.ChapterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // thêm import này

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ChapterRepository chapterRepository; // thêm

    /**
     * Tạo khóa học mới
     */
    public CourseDTO createCourse(CreateCourseRequest request) {
        Course course = new Course();
        course.setCourseCode(request.getCourseCode());
        course.setStartDate(request.getStartDate());
        course.setEndDate(request.getEndDate());
        course.setContent(request.getContent());

        Course savedCourse = courseRepository.save(course);
        return mapToDTO(savedCourse);
    }

    /**
     * Lấy tất cả khóa học
     */
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lấy một khóa học theo ID
     */
    public CourseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return mapToDTO(course);
    }

    /**
     * Cập nhật một khóa học
     */
    public CourseDTO updateCourse(Long id, UpdateCourseRequest request) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        existingCourse.setCourseCode(request.getCourseCode());
        existingCourse.setStartDate(request.getStartDate());
        existingCourse.setEndDate(request.getEndDate());
        existingCourse.setContent(request.getContent());

        Course updatedCourse = courseRepository.save(existingCourse);
        return mapToDTO(updatedCourse);
    }

    /**
     * Xóa một khóa học (XÓA CẢ CHAPTERS TRƯỚC)
     */
    @Transactional
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        // Xóa tất cả chapters thuộc khóa học này
        chapterRepository.deleteByCourseId(id);

        // Nếu sau này có enrollments:
        // enrollmentRepository.deleteByCourseId(id);

        // Xóa khóa học
        courseRepository.deleteById(id);
    }

    // ----- HELPER -----
    private CourseDTO mapToDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setCourseCode(course.getCourseCode());
        dto.setStartDate(course.getStartDate());
        dto.setEndDate(course.getEndDate());
        dto.setContent(course.getContent());
        return dto;
    }
}