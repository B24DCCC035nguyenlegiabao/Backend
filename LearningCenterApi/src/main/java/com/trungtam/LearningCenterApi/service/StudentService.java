// Đặt tại: com.trungtam.LearningCenterApi.service.StudentService.java
package com.trungtam.LearningCenterApi.service;

import com.trungtam.LearningCenterApi.dto.CreateStudentRequest;
import com.trungtam.LearningCenterApi.dto.StudentDTO;
import com.trungtam.LearningCenterApi.dto.UpdateStudentRequest;
import com.trungtam.LearningCenterApi.entity.Student;
import com.trungtam.LearningCenterApi.exception.ResourceNotFoundException;
import com.trungtam.LearningCenterApi.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    /**
     * HÀM 1: Thêm mới học viên
     * (ĐÃ FIX LOGIC MSV DỰA TRÊN MAX ID)
     */
    public StudentDTO createStudent(CreateStudentRequest request) {
        // 1. Logic tự sinh mã MSV (Dựa trên ID lớn nhất hiện tại)
        // Nếu không có bản ghi nào (orElse(0L)), thì bắt đầu từ 1L.
        Long nextSequentialNumber = studentRepository.findTopByOrderByIdDesc()
                .map(Student::getId)
                .orElse(0L) + 1;

        String msv = "HV" + String.format("%05d", nextSequentialNumber);

        // 2. Chuyển từ DTO (Request) sang Entity
        Student student = new Student();
        student.setMsv(msv);
        student.setHo(request.getHo());
        student.setTen(request.getTen());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setHometown(request.getHometown());
        student.setResidenceProvince(request.getResidenceProvince());

        // 3. Lưu vào DB
        Student savedStudent = studentRepository.save(student);

        // 4. Chuyển từ Entity sang DTO (Response)
        return convertToDto(savedStudent);
    }

    /**
     * HÀM 2: Lấy danh sách VÀ tìm kiếm (Giữ nguyên)
     */
    public List<StudentDTO> searchStudents(String keyword) {
        List<Student> students;
        if (keyword == null || keyword.isBlank()) {
            students = studentRepository.findAll();
        } else {
            // Nếu có tìm kiếm -> gọi hàm trong repository
            students = studentRepository.findByMsvContainingOrHoContainingOrTenContaining(keyword, keyword, keyword);
        }

        // Chuyển List<Student> (Entity) sang List<StudentDTO>
        return students.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * HÀM 3: Lấy 1 học viên (Giữ nguyên)
     */
    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return convertToDto(student);
    }

    /**
     * HÀM 4: Cập nhật học viên (Giữ nguyên)
     */
    public StudentDTO updateStudent(Long id, UpdateStudentRequest request) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        existingStudent.setHo(request.getHo());
        existingStudent.setTen(request.getTen());
        existingStudent.setDateOfBirth(request.getDateOfBirth());
        existingStudent.setHometown(request.getHometown());
        existingStudent.setResidenceProvince(request.getResidenceProvince());

        Student updatedStudent = studentRepository.save(existingStudent);

        return convertToDto(updatedStudent);
    }

    /**
     * HÀM 5: Xóa học viên (Giữ nguyên)
     */
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }


    /**
     * HÀM HỖ TRỢ: Chuyển đổi Entity sang DTO (Giữ nguyên)
     */
    private StudentDTO convertToDto(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setMsv(student.getMsv());
        dto.setHo(student.getHo());
        dto.setTen(student.getTen());
        dto.setFullName(student.getHo() + " " + student.getTen());
        dto.setDateOfBirth(student.getDateOfBirth());
        dto.setHometown(student.getHometown());
        dto.setResidenceProvince(student.getResidenceProvince());
        return dto;
    }
}