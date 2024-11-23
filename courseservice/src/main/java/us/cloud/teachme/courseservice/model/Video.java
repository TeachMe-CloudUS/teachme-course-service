package us.cloud.teachme.courseservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "video")
public class Video {
    @Id
    private String id;

    @Size(min = 3, max = 100, message = "The video title must be between 3 and 100 characters")
    private String title;

    @Size(min = 3, max = 1000, message = "The video description must be between 3 and 1000 characters")
    private String description;

    @Size(min = 3, max = 1000, message = "The video URL must be between 3 and 1000 characters")
    private String url;

}