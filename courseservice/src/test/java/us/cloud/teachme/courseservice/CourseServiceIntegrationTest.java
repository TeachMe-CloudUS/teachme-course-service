package us.cloud.teachme.courseservice;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import us.cloud.teachme.courseservice.model.Course;
import us.cloud.teachme.courseservice.model.Level;
import us.cloud.teachme.courseservice.repository.CourseRepository;


@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class CourseServiceIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.6");

    @Autowired
    private CourseRepository courseRepository;

    @BeforeAll
    static void setup() {
        mongoDBContainer.start();
        System.setProperty("spring.data.mongodb.uri", mongoDBContainer.getReplicaSetUrl());
    }

    @Test
    void testInsertAndFindCourse() {
        // Arrange
        Course course = new Course();
        course.setId("1");
        course.setName("Java Basics");
        course.setDescription("Learn Java");
        course.setCategory("Programming");
        course.setDuration("10h");
        course.setCreationDate(new Date());
        course.setLastModifDate(new Date());
        course.setLevel(Level.BEGINNER);
        course.setAdditionalResources(List.of());
        course.setRating(4.5);

        // Act
        courseRepository.save(course);
        List<Course> courses = courseRepository.findAll();

        // Assert
        Assertions.assertFalse(courses.isEmpty());
        Assertions.assertEquals("Java Basics", courses.get(0).getName());
    }

    @Test
    void testUpdateCourse() {
        // Arrange
        Course course = new Course();
            course.setId("1");
            course.setName("Java Basics");
            course.setDescription("Learn Java");
            course.setCategory("Programming");
            course.setDuration("10h");
            course.setCreationDate(new Date());
            course.setLastModifDate(new Date());
            course.setLevel(Level.BEGINNER);
            course.setAdditionalResources(List.of());
            course.setRating(4.5);
        course = courseRepository.save(course);

        // Act
        course.setCategory("Funciona");
        courseRepository.save(course);

        // Assert
        Optional<Course> updatedCourse = courseRepository.findById(course.getId());
        Assertions.assertTrue(updatedCourse.isPresent());
        Assertions.assertEquals("Funciona", updatedCourse.get().getCategory());
    }
    @Test
    void testDeleteCourse() {
        // Arrange
        Course course = new Course();
            course.setId("1");
            course.setName("Java Basics");
            course.setDescription("Learn Java");
            course.setCategory("Programming");
            course.setDuration("10h");
            course.setCreationDate(new Date());
            course.setLastModifDate(new Date());
            course.setLevel(Level.BEGINNER);
            course.setAdditionalResources(List.of());
            course.setRating(4.5);
        course = courseRepository.save(course);

        // Act
        courseRepository.deleteById(course.getId());

        // Assert
        Optional<Course> deletedCourse = courseRepository.findById(course.getId());
        Assertions.assertFalse(deletedCourse.isPresent());
    }
    @Test
    void testFindByCategory() {
        // Arrange
        Course course = new Course();
        course.setId("1");
        course.setName("Java Basics");
        course.setDescription("Learn Java");
        course.setCategory("Programming");
        course.setDuration("10h");
        course.setCreationDate(new Date());
        course.setLastModifDate(new Date());
        course.setLevel(Level.BEGINNER);
        course.setAdditionalResources(List.of());
        course.setRating(4.5);
        courseRepository.save(course);

        // Act
        List<Course> foundCourses = courseRepository.findByCategory("Programming");

        // Assert
        Assertions.assertFalse(foundCourses.isEmpty(), "No courses found for the specified category.");
        for (Course foundCourse : foundCourses) {
            Assertions.assertEquals("Programming", foundCourse.getCategory());
        }
    }
    @Test
    void testFindAllCourses() {
        // Arrange
        Course course = new Course();
        course.setId("1");
        course.setName("Java Basics");
        course.setDescription("Learn Java");
        course.setCategory("Programming");
        course.setDuration("10h");
        course.setCreationDate(new Date());
        course.setLastModifDate(new Date());
        course.setLevel(Level.BEGINNER);
        course.setAdditionalResources(List.of());
        course.setRating(4.5);
        courseRepository.save(course);
        Course course1 = new Course();
        course1.setId("2");
        course1.setName("Java Basics2");
        course1.setDescription("Learn Java2");
        course1.setCategory("Programming");
        course1.setDuration("10h");
        course1.setCreationDate(new Date());
        course1.setLastModifDate(new Date());
        course1.setLevel(Level.BEGINNER);
        course1.setAdditionalResources(List.of());
        course1.setRating(4.5);
        courseRepository.save(course1);

        // Act
        List<Course> courses = courseRepository.findAll();

        // Assert
        Assertions.assertEquals(2, courses.size());
    }

    @Test
    void testCourseNotFound() {
        // Act
        Optional<Course> course = courseRepository.findById("nonexistent-id");

        // Assert
        Assertions.assertFalse(course.isPresent());
    }

    @Test
    void testSaveInvalidCourseWithManualValidation() {
    // Arrange
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();

    Course course = new Course();
    course.setDescription("Description without a name");

    // Act
    Set<ConstraintViolation<Course>> violations = validator.validate(course);

    // Assert
    Assertions.assertFalse(violations.isEmpty());
    Assertions.assertTrue(
        violations.stream().anyMatch(v -> v.getMessage().equals("The course title is mandatory"))
    );
}



}

