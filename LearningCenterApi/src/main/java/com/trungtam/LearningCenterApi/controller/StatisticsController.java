package com.trungtam.LearningCenterApi.controller;
import com.trungtam.LearningCenterApi.dto.CourseStatsDTO;
import com.trungtam.LearningCenterApi.dto.DashboardSummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.trungtam.LearningCenterApi.service.StatisticsService;
import java.util.List;
import java.util.Map; // Ví dụ về kiểu trả về linh hoạt

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/stats")
@PreAuthorize("isAuthenticated()")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * API Thống kê học viên theo tỉnh
     * Trả về: {"Hà Nội": 15, "TP. HCM": 20}
     */
    @GetMapping("/students-by-province")
    public ResponseEntity<Map<String, Long>> getStudentStatsByProvince() {
        Map<String, Long> stats = statisticsService.countStudentsByProvince();
        return ResponseEntity.ok(stats);
    }

    /**
     * API Thống kê khóa học theo năm
     * URL: /api/v1/stats/courses-by-year?year=2025
     */
    @GetMapping("/courses-by-year")
    public ResponseEntity<CourseStatsDTO> getCourseStatsByYear(
            @RequestParam int year
    ) {
        CourseStatsDTO stats = statisticsService.getCourseStatsByYear(year);
        return ResponseEntity.ok(stats);
    }

    /**
     * API Thống kê tổng quan cho Dashboard
     */
    @GetMapping("/dashboard-summary")
    public ResponseEntity<DashboardSummaryDTO> getDashboardSummary() {
        DashboardSummaryDTO summary = statisticsService.getDashboardSummary();
        return ResponseEntity.ok(summary);
    }
}