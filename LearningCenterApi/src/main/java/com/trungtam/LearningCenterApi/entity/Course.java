package com.trungtam.LearningCenterApi.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID") // Khớp "ID"
    private Long id;

    @Column(name = "MaKhoaHoc",unique = true, nullable = false) // Khớp "MaKhoaHoc"
    private String courseCode; // Mã khóa

    @Column(name = "NgayBatDau") // Khớp "NgayBatDau"
    private LocalDateTime startDate;

    @Column(name = "NgayKetThuc") // Khớp "NgayKetThuc"
    private LocalDateTime endDate;

    @Column(name = "NoiDung", columnDefinition = "TEXT") // Khớp "NoiDung"
    private String content; // Nội dung khóa học

    // 1 Khóa học có Nhiều lượt đăng ký
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Enrollment> enrollments;

    // 1 Khóa học có Nhiều chương
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Chapter> chapters;
}