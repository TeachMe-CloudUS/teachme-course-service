package us.cloud.teachme.courseservice.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import us.cloud.teachme.courseservice.model.Course;
import us.cloud.teachme.courseservice.repository.CourseRepository;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(String id) {
        return courseRepository.findById(id);
    }

    public List<Course> getCoursesByCategory(String category) {
        return courseRepository.findByCategory(category);
    }

    public Course createCourse(Course course) {
        course.setCreationDate(new Date());
        if (courseRepository.existsByName(course.getName())) {
            throw new IllegalArgumentException("A course with the same name already exists");
        }
        return courseRepository.save(course);
    }

    public Course updateCourse(String id, Course updatedCourse) {
        if (courseRepository.existsByName(updatedCourse.getName())) {
            throw new IllegalArgumentException("A course with the same name already exists");
        }
        return courseRepository.findById(id).map(course -> {
            course.setName(updatedCourse.getName());
            course.setDescription(updatedCourse.getDescription());
            course.setCategory(updatedCourse.getCategory());
            course.setDuration(updatedCourse.getDuration());
            course.setLastModifDate(new Date());
            course.setLevel(updatedCourse.getLevel());
            return courseRepository.save(course);
        }).orElseThrow(() -> new RuntimeException("Course not found with id " + id));
    }

    public Course actualizarRating(String id, double nuevoRating) {
        Optional<Course> CourseOptional = courseRepository.findById(id);
        if (CourseOptional.isPresent()) {
            Course Course = CourseOptional.get();
            Course.setRating(nuevoRating);  // Actualizar el atributo
            return courseRepository.save(Course);  // Guardar los cambios
        }
        return null;  // Si no existe el curso
    }

    public void deleteCourse(String id) {
        if (courseRepository.existsById(id)) {
            courseRepository.deleteById(id);
        } else {
            throw new RuntimeException("Course not found with id " + id);
        }
    }

}
