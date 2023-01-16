//package com.ayush.ravan.services;
//
//import com.ayush.ravan.exceptions.CustomException;
//import com.ayush.ravan.model.Course;
//import com.ayush.ravan.repository.CourseRepository;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.ResponseStatus;
//
//import javax.annotation.Resource;
//@ResponseStatus(HttpStatus.BAD_REQUEST)
//public class CourseServiceImpl implements CourseService{
//    @Resource
//    private CourseRepository courseRepository;
//    @Override
//    public void deleteCourse(String courseName) {
//        final Course course = courseRepository.findByCourseName(courseName)
//                .orElseThrow(() -> new CustomException("Course not found !"));
//
//        course.removeStudents();
//        courseRepository.deleteById(course.getId());
//    }
//}
