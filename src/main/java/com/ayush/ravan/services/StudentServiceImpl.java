//package com.ayush.ravan.services;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//import javax.annotation.Resource;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import com.ayush.ravan.dto.StudentDto;
//import com.ayush.ravan.model.Course;
//import com.ayush.ravan.model.Student;
//import com.ayush.ravan.repository.CourseRepository;
//import com.ayush.ravan.repository.StudentRepository;
//@Service
//public class StudentServiceImpl implements StudentService {
//    @Resource
//    private StudentRepository studentRepository;
//    @Resource
//    private CourseRepository courseRepository;
//    @Transactional
//    @Override
//    public StudentDto addStudent(StudentDto studentDto) {
//        Student student = new Student();
//        mapDtoToEntity(studentDto, student);
//        Student savedStudent = studentRepository.save(student);
//        return mapEntityToDto(savedStudent);
//    }
//    @Override
//    public List<StudentDto> getAllStudents() {
//        List<StudentDto> studentDtos = new ArrayList<>();
//        List<Student> students = studentRepository.findAll();
//        students.stream().forEach(student -> {
//            StudentDto studentDto = mapEntityToDto(student);
//            studentDtos.add(studentDto);
//        });
//        return studentDtos;
//    }
//    @Transactional
//    @Override
//    public StudentDto updateStudent(Integer id, StudentDto studentDto) {
//        Student std = studentRepository.getOne(id);
//        std.getCourses().clear();
//        mapDtoToEntity(studentDto, std);
//        Student student = studentRepository.save(std);
//        return mapEntityToDto(student);
//    }
//    @Override
//    public String deleteStudent(Integer studentId) {
//        Optional<Student> student = studentRepository.findById(studentId);
//        //Remove the related courses from student entity.
//        if(student.isPresent()) {
//            student.get().removeCourses();
//            studentRepository.deleteById(student.get().getId());
//            return "Student with id: " + studentId + " deleted successfully!";
//        }
//        return null;
//    }
//    private void mapDtoToEntity(StudentDto studentDto, Student student) {
//        student.setFirstName(studentDto.getFirstName());
//        student.setLastName(studentDto.getLastName());
//        if (null == student.getCourses()) {
//            student.setCourses(new HashSet<>());
//        }
////        studentDto.getCourses().stream().forEach(courseName -> {
////            Course course = courseRepository.findByCourseName(courseName);
////            if (null == course) {
////                course = new Course();
////                course.setStudents(new HashSet<>());
////            }
////            course.setCourseName(courseName);
////            student.addCourse(course);
////        });
//    }
//    private StudentDto mapEntityToDto(Student student) {
//        StudentDto responseDto = new StudentDto();
//        responseDto.setFirstName(student.getFirstName());
//        responseDto.setLastName(student.getLastName());
//        responseDto.setId(student.getId());
//        responseDto.setCourses(student.getCourses().stream().map(Course::getCourseName).collect(Collectors.toSet()));
//        return responseDto;
//    }
//}
