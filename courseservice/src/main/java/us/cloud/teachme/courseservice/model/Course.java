package us.cloud.teachme.courseservice.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "course")
public class Course {
    @Id
    private String id;
    @NotBlank(message = "The course title is mandatory")
    @Size(min = 3, max = 100, message = "The course title must be between 3 and 100 characters")
    private String name;
    @NotBlank(message = "The course description is mandatory")
    @Size(min = 3, max = 1000, message = "The course description must be between 3 and 1000 characters")
    private String description;
    @NotNull(message = "The category is mandatory")
    private String category;
    @NotNull(message = "The duration is mandatory")
    private String duration;
    private Date creationDate;
    private Date lastModifDate;
    @NotNull(message = "The level is mandatory")
    private Level level;

    private List<Video> additionalResources;

}
