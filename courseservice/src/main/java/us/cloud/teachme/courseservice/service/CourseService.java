package us.cloud.teachme.courseservice.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import us.cloud.teachme.courseservice.model.Course;
import us.cloud.teachme.courseservice.repository.CourseRepository;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${forumCreate.url}")
    private String forumCreateUrl;

    @Value("${forumDelete.url}")
    private String forumDeleteUrl;

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
        // Configurar la fecha de creación
        course.setCreationDate(new Date());
    
        // Verificar si ya existe un curso con el mismo nombre
        if (courseRepository.existsByName(course.getName())) {
            throw new IllegalArgumentException("A course with the name '" + course.getName() + "' already exists.");
        }
    
        // Guardar el nuevo curso en el repositorio
        Course nuevoCurso = courseRepository.save(course);
    
        // Preparar el payload para el microservicio
        String urlDestino = forumCreateUrl; // Asegúrate de que esta variable tenga el valor correcto
        Map<String, Object> payload = Map.of(
            "courseId", nuevoCurso.getId(),
            "name", nuevoCurso.getName()
        );
    
        // Llamada al microservicio externo
        webClientBuilder.build().post()
            .uri(urlDestino)
            .bodyValue(payload)
            .retrieve()
            .toBodilessEntity()
            .block();
    
        // Retornar el curso creado
        return nuevoCurso;
    }
    

    public Course updateCourse(String id, Course updatedCourse) {
        return courseRepository.findById(id).map(course -> {
            // Usar valores existentes si no se proporcionan
            // Actualizar los demás campos
            course.setName(course.getName());
            course.setDescription(course.getDescription());
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
        // Verificar si el curso existe
        if (!courseRepository.existsById(id)) {
            throw new RuntimeException("Course not found with id " + id);
        }
    
        // Eliminar el curso del repositorio
        courseRepository.deleteById(id);
    
        // Preparar la URL del microservicio
        String urlDestino = forumDeleteUrl + id; // Suponiendo que el id debe ir en el path
    
        try {
            // Llamada al microservicio de eliminación
            webClientBuilder.build().delete()
                .uri(urlDestino)
                .retrieve()
                .toBodilessEntity()
                .block();
        } catch (Exception e) {
            // Manejo de errores
            throw new RuntimeException("Error calling forum delete microservice: " + e.getMessage(), e);
        }
    }
    

}
