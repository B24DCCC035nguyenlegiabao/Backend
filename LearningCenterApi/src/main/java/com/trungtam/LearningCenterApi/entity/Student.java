package com.trungtam.LearningCenterApi.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.Set;

@Data
@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID") // Khớp "ID"
    private Long id;

    @Column(name = "Msv", unique = true, nullable = false) // Khớp "Msv"
    private String msv; // Mã học viên (tự sinh)

    @Column(name = "Ho", nullable = false) // Khớp "Ho"
    private String ho;

    @Column(name = "Ten", nullable = false) // Khớp "Ten"
    private String ten;

    @Column(name = "NgaySinh") // Khớp "NgaySinh"
    private LocalDate dateOfBirth;

    @Column(name = "QueQuan") // Khớp "QueQuan"
    private String hometown; // Quê quán

    @Column(name = "TinhThuongTru") // Khớp "TinhThuongTru"
    private String residenceProvince; // Tỉnh thường trú

    // Đánh dấu quan hệ: 1 Học viên có thể có Nhiều lượt đăng ký
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Enrollment> enrollments;
}