package us.cloud.teachme.courseservice.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "course")
public class Course {
    @Id
    private String id;
    private String name;
    private String description;
    private String category;
    private String duration;
    private Date creationDate;
    private Date lastModifDate;
    private Level level;


    
}
