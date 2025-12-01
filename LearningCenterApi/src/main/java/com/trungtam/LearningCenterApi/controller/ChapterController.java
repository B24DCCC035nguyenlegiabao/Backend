package com.trungtam.LearningCenterApi.controller;

import com.trungtam.LearningCenterApi.dto.ChapterDTO;
import com.trungtam.LearningCenterApi.dto.CreateChaptersRequest;
import com.trungtam.LearningCenterApi.entity.Chapter;
import com.trungtam.LearningCenterApi.entity.Course;
import com.trungtam.LearningCenterApi.repository.ChapterRepository;
import com.trungtam.LearningCenterApi.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/chapters")
@PreAuthorize("isAuthenticated()")
public class ChapterController {

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private CourseRepository courseRepository;

    // Admin: tạo N chương rỗng cho một khóa học
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ChapterDTO>> createChapters(@Valid @RequestBody CreateChaptersRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        List<Chapter> created = new ArrayList<>();
        int start = chapterRepository.findByCourseIdOrderByNumberAsc(course.getId()).size() + 1;
        for (int i = 0; i < request.getChapterCount(); i++) {
            Chapter ch = new Chapter();
            ch.setCourse(course);
            ch.setNumber(start + i);
            ch.setTitle("");
            ch.setContent("");
            created.add(chapterRepository.save(ch));
        }

        List<ChapterDTO> dtos = created.stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.CREATED).body(dtos);
    }

    // Mọi user đã đăng nhập có thể xem danh sách chương
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<ChapterDTO>> getChaptersByCourse(@PathVariable Long courseId) {
        List<Chapter> chapters = chapterRepository.findByCourseIdOrderByNumberAsc(courseId);
        List<ChapterDTO> dtos = chapters.stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Admin cập nhật tiêu đề và nội dung chương
    @PutMapping("/{chapterId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChapterDTO> updateChapter(@PathVariable Long chapterId, @Valid @RequestBody ChapterDTO dto) {
        Chapter ch = chapterRepository.findById(chapterId).orElseThrow(() -> new RuntimeException("Chapter not found"));
        ch.setTitle(dto.getTitle());
        ch.setContent(dto.getContent());
        Chapter saved = chapterRepository.save(ch);
        return ResponseEntity.ok(toDto(saved));
    }

    private ChapterDTO toDto(Chapter ch) {
        ChapterDTO dto = new ChapterDTO();
        dto.setId(ch.getId());
        dto.setNumber(ch.getNumber());
        dto.setTitle(ch.getTitle());
        dto.setContent(ch.getContent());
        return dto;
    }
}

