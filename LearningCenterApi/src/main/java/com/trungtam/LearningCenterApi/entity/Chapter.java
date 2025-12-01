package com.trungtam.LearningCenterApi.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "chapters")
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Course_ID", nullable = false)
    private Course course;

    @Column(name = "SoChuong", nullable = false)
    private Integer number; // Số chương (1..N)

    @Column(name = "TieuDe")
    private String title;

    @Column(name = "NoiDung", columnDefinition = "TEXT")
    private String content;
}

