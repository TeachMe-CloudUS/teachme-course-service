package us.cloud.teachme.courseservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
@EnableCaching
public class CourseserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CourseserviceApplication.class, args);
	}

}
