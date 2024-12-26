package us.cloud.teachme.courseservice.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import us.cloud.teachme.courseservice.model.Course;

public interface CourseRepository extends MongoRepository<Course, String>{
    List<Course> findByCategory(String category);

    boolean existsByName(String name);
}
