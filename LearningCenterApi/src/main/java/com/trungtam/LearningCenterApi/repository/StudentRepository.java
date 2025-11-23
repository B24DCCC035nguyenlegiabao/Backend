package com.trungtam.LearningCenterApi.repository;

import com.trungtam.LearningCenterApi.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Yêu cầu: "Tìm kiếm học viên dựa trên mã hoặc tên"
    // Spring tự hiểu: "findBy" + "Msv" + "Containing" (chứa) + "Or" + "FullName" + "Containing"
    // -> SELECT * FROM students WHERE msv LIKE '%keyword%' OR full_name LIKE '%keyword%'
    List<Student> findByMsvContainingOrHoContainingOrTenContaining(String msv, String ho, String ten);
    // THÊM DÒNG NÀY: Tìm bản ghi có ID lớn nhất, sắp xếp giảm dần (DESC) và lấy 1 bản ghi đầu (Top)
    Optional<Student> findTopByOrderByIdDesc();
    // Yêu cầu: "Thống kê học viên theo quê quán, tỉnh thường trú"
    // Khi dùng "findBy..." không đủ, chúng ta dùng @Query
    // JPQL (Java Persistence Query Language) - viết query dựa trên TÊN LỚP và TÊN THUỘC TÍNH
    @Query("SELECT s.residenceProvince, COUNT(s) FROM Student s WHERE s.residenceProvince IS NOT NULL GROUP BY s.residenceProvince")
    List<Object[]> countStudentsByProvince();

    // Hàm trên sẽ trả về List<Object[]>
    // Ví dụ: [ ["Hà Nội", 15], ["Hải Phòng", 10], ["Đà Nẵng", 8] ]
}