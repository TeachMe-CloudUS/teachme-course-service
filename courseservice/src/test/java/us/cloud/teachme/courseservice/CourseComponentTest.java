package us.cloud.teachme.courseservice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import us.cloud.teachme.courseservice.model.Course;
import us.cloud.teachme.courseservice.model.Level;
import us.cloud.teachme.courseservice.model.Video;
import us.cloud.teachme.courseservice.service.CourseService;
import us.cloud.teachme.courseservice.service.UserService;
import us.cloud.teachme.courseservice.service.VideoService;

@SpringBootTest
@AutoConfigureMockMvc
public class CourseComponentTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @MockBean
    private UserService userService;

    @MockBean
    private VideoService videoService;

    @Test
    void testGetAllCourses() throws Exception {
        Course course1 = new Course();
        course1.setId("1");
        course1.setName("Java Basics");
        course1.setDescription("Learn Java");
        course1.setCategory("Programming");
        course1.setDuration("10h");
        course1.setCreationDate(new Date());
        course1.setLastModifDate(new Date());
        course1.setLevel(Level.BEGINNER);
        course1.setAdditionalResources(List.of());
        course1.setRating(4.5);

        Course course2 = new Course();
        course2.setId("2");
        course2.setName("Java Basics");
        course2.setDescription("Learn Spring");
        course2.setCategory("Programming");
        course2.setDuration("20h");
        course2.setCreationDate(new Date());
        course2.setLastModifDate(new Date());
        course2.setLevel(Level.BEGINNER);
        course2.setAdditionalResources(List.of());
        course2.setRating(4.5);

        List<Course> courses = new ArrayList<>();
        courses.add(course1);
        courses.add(course2);

        Mockito.when(courseService.getAllCourses()).thenReturn(courses);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/courses"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Java Basics"));
    }

    @Test
    void testGetCourseById_ValidId() throws Exception {
        Course course1 = new Course();
        course1.setId("1");
        course1.setName("Java Basics");
        course1.setDescription("Learn Java");
        course1.setCategory("Programming");
        course1.setDuration("10h");
        course1.setCreationDate(new Date());
        course1.setLastModifDate(new Date());
        course1.setLevel(Level.BEGINNER);
        course1.setAdditionalResources(List.of());
        course1.setRating(4.5);

        Mockito.when(courseService.getCourseById("1")).thenReturn(Optional.of(course1));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/courses/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Java Basics"));
    }

    @Test
    void testGetCourseById_InvalidId() throws Exception {
        Mockito.when(courseService.getCourseById("999")).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/courses/999"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testCreateCourse_WithAdminRole() throws Exception {
        String token = "Bearer validToken";
        Course course = new Course();
        course.setName("Java Basics");
        course.setDescription("Learn Java");
        course.setCategory("Programming");
        course.setDuration("10h");
        course.setCreationDate(null);
        course.setLastModifDate(null);
        course.setLevel(Level.BEGINNER);
        course.setAdditionalResources(null);
        course.setRating(null);

        Video video = new Video();
        video.setTitle("Video 1");
        video.setDescription("Video 1 description");
        video.setUrl("https://example.com/video1");
        

        Mockito.when(userService.extractUserId(Mockito.anyString())).thenReturn("adminUserId");
        Mockito.when(userService.getUserRoleById(Mockito.eq("adminUserId"), Mockito.anyString())).thenReturn("ADMIN");
        Mockito.when(courseService.createCourse(Mockito.any(Course.class))).thenAnswer(invocation -> {
            Course courseArg = invocation.getArgument(0);
            courseArg.setId("1");
            return courseArg;
        });
        Mockito.when(videoService.searchVideos(Mockito.anyString())).thenReturn(List.of(video));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/courses")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(course)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"));
    }

    @Test
    void testCreateCourse_WithoutAdminRole() throws Exception {
        String token = "Bearer validToken";
        Course course = new Course();
        course.setId(null);
        course.setName("Java Basics");
        course.setDescription("Learn Java");
        course.setCategory("Programming");
        course.setDuration("10h");
        course.setCreationDate(null);
        course.setLastModifDate(null);
        course.setLevel(Level.BEGINNER);
        course.setAdditionalResources(null);
        course.setRating(null);

        

        Mockito.when(userService.extractUserId(Mockito.anyString())).thenReturn("userId");
        Mockito.when(userService.getUserRoleById(Mockito.eq("userId"), Mockito.anyString())).thenReturn("USER");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/courses")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(course)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void testUpdateRating_ValidCourse() throws Exception {
        Course course = new Course();
        course.setId("1");
        course.setName("Java Basics");
        course.setDescription("Learn Java");
        course.setCategory("Programming");
        course.setDuration("10h");
        course.setCreationDate(null);
        course.setLastModifDate(null);
        course.setLevel(Level.BEGINNER);
        course.setAdditionalResources(null);
        course.setRating(4.0);

        Course updatedCourse = new Course();
        updatedCourse.setId("1");
        updatedCourse.setName("Java Basics");
        updatedCourse.setDescription("Learn Java");
        updatedCourse.setCategory("Programming");
        updatedCourse.setDuration("10h");
        updatedCourse.setCreationDate(null);
        updatedCourse.setLastModifDate(null);
        updatedCourse.setLevel(Level.BEGINNER);
        updatedCourse.setAdditionalResources(null);
        updatedCourse.setRating(4.5);
        
        
        Mockito.when(courseService.actualizarRating("1", 4.5)).thenReturn(updatedCourse);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/courses/1/rating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(Map.of("rating", 4.5))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.rating").value(4.5));
    }

    @Test
    void testUpdateRating_InvalidCourse() throws Exception {
        Mockito.when(courseService.actualizarRating("999", 4.5)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/courses/999/rating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(Map.of("rating", 4.5))))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testUpdateCourse_WithAdminRole() throws Exception {
        String token = "Bearer validToken";

        Course existingCourse = new Course();
        existingCourse.setId("1");
        existingCourse.setName("Java Basics");
        existingCourse.setDescription("Learn Java");
        existingCourse.setCategory("Programming");
        existingCourse.setDuration("10h");
        existingCourse.setCreationDate(new Date());
        existingCourse.setLastModifDate(new Date());
        existingCourse.setLevel(Level.BEGINNER);
        existingCourse.setAdditionalResources(List.of());
        existingCourse.setRating(4.5);


        Course updatedCourse = new Course();
        updatedCourse.setId("1");
        updatedCourse.setName("Advanced Java");
        updatedCourse.setDescription("Learn advanced Java concepts");
        updatedCourse.setCategory("Programming");
        updatedCourse.setDuration("15h");
        updatedCourse.setCreationDate(existingCourse.getCreationDate());
        updatedCourse.setLastModifDate(new Date());
        updatedCourse.setLevel(Level.ADVANCE);
        updatedCourse.setAdditionalResources(List.of());
        updatedCourse.setRating(4.8);


        Mockito.when(userService.extractUserId(Mockito.anyString())).thenReturn("adminUserId");
        Mockito.when(userService.getUserRoleById(Mockito.eq("adminUserId"), Mockito.anyString())).thenReturn("ADMIN");

        Mockito.when(courseService.updateCourse(Mockito.eq("1"), Mockito.any(Course.class)))
                .thenReturn(updatedCourse);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/courses/{id}", "1")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedCourse)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Advanced Java"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Learn advanced Java concepts"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.category").value("Programming"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.duration").value("15h"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.level").value("ADVANCE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.rating").value(4.8));
    }

    @Test
    void testUpdateCourse_WithUserRole_ShouldFail() throws Exception {
        String token = "Bearer validToken";

        // Datos actualizados enviados por el usuario
        Course updatedCourse = new Course();
        updatedCourse.setId("1");
        updatedCourse.setName("Advanced Java");
        updatedCourse.setDescription("Learn advanced Java concepts");
        updatedCourse.setCategory("Programming");
        updatedCourse.setDuration("15h");
        updatedCourse.setLevel(Level.ADVANCE);

        // Mock del servicio de usuario: usuario con rol `USER` en lugar de `ADMIN`
        Mockito.when(userService.extractUserId(Mockito.anyString())).thenReturn("userId");
        Mockito.when(userService.getUserRoleById(Mockito.eq("userId"), Mockito.anyString())).thenReturn("USER");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/courses/{id}", "1")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedCourse)))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andDo(MockMvcResultHandlers.print());
    }
    @Test
    void testUpdateCourse_NonExistingCourse_ShouldFail() throws Exception {
        String token = "Bearer validToken";

        // Datos actualizados enviados por el usuario
        Course updatedCourse = new Course();
        updatedCourse.setId("999"); // ID inexistente
        updatedCourse.setName("Advanced Java");
        updatedCourse.setDescription("Learn advanced Java concepts");
        updatedCourse.setCategory("Programming");
        updatedCourse.setDuration("15h");
        updatedCourse.setLevel(Level.ADVANCE);

        // Mock del servicio de usuario: usuario con rol `ADMIN`
        Mockito.when(userService.extractUserId(Mockito.anyString())).thenReturn("adminUserId");
        Mockito.when(userService.getUserRoleById(Mockito.eq("adminUserId"), Mockito.anyString())).thenReturn("ADMIN");

        // Mock del servicio de curso: ID inexistente lanza una excepción
        Mockito.when(courseService.updateCourse(Mockito.eq("999"), Mockito.any(Course.class)))
                .thenThrow(new RuntimeException("Course not found with ID 999"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/courses/{id}", "999")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedCourse)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testDeleteCourse_WithAdminRole() throws Exception {
        String token = "Bearer validToken";
        Course course = new Course();
        course.setId("1");
        course.setName("Java Basics");
        course.setDescription("Learn Java");
        course.setCategory("Programming");
        course.setDuration("10h");
        course.setCreationDate(null);
        course.setLastModifDate(null);
        course.setLevel(Level.BEGINNER);
        course.setAdditionalResources(null);
        course.setRating(4.0);


        Mockito.when(userService.extractUserId(Mockito.anyString())).thenReturn("adminUserId");
        Mockito.when(userService.getUserRoleById(Mockito.eq("adminUserId"), Mockito.anyString())).thenReturn("ADMIN");

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/courses/1")
                        .header("Authorization", token))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void testDeleteCourse_WithoutAdminRole() throws Exception {
        String token = "Bearer validToken";
        Course course = new Course();
        course.setId("1");
        course.setName("Java Basics");
        course.setDescription("Learn Java");
        course.setCategory("Programming");
        course.setDuration("10h");
        course.setCreationDate(null);
        course.setLastModifDate(null);
        course.setLevel(Level.BEGINNER);
        course.setAdditionalResources(null);
        course.setRating(4.0);


        Mockito.when(userService.extractUserId(Mockito.anyString())).thenReturn("userId");
        Mockito.when(userService.getUserRoleById(Mockito.eq("userId"), Mockito.anyString())).thenReturn("USER");

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/courses/1")
                        .header("Authorization", token))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
    
}






