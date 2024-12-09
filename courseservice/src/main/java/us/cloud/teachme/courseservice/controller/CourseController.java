package us.cloud.teachme.courseservice.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import us.cloud.teachme.courseservice.model.Course;
import us.cloud.teachme.courseservice.model.Video;
import us.cloud.teachme.courseservice.service.CourseService;
import us.cloud.teachme.courseservice.service.UserService;
import us.cloud.teachme.courseservice.service.VideoService;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {

    @Autowired
    private final VideoService videoService;
    private final CourseService courseService;
    private final UserService userService;

    public CourseController(VideoService videoService, CourseService courseService, UserService userService) {
        this.videoService = videoService;
        this.courseService = courseService;
        this.userService = userService;
    }

    // GET /api/courses - Obtiene todos los cursos
    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/filter")
    public List<Course> getCoursesByCategory(@RequestParam String category) {
        return courseService.getCoursesByCategory(category);
    }

    // GET /api/courses/{id} - Obtiene un curso por ID
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable String id) {
        return courseService.getCourseById(id)
                .map(course -> ResponseEntity.ok(course))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/list")
    public ResponseEntity<List<Course>> getCoursesById(@RequestBody List<String> courseIds) {
        return ResponseEntity.ok(
                courseIds.stream().map(
                                courseId -> courseService.getCourseById(courseId).orElse(null))
                        .collect(Collectors.toList()));
    }

    // POST /api/courses - Crea un nuevo curso
    @PostMapping
    @CircuitBreaker(name = "createCourse", fallbackMethod = "controllerFallback")
    public ResponseEntity<?> createCourse(@RequestHeader("Authorization") String token, @RequestBody Course course) {
            // Eliminar cualquier espacio extra del token
        token = token.trim();

            // Extraer el UserId del token
        String userId = userService.extractUserId(token);
        System.out.println("UserId extraído: " + userId);

            // Consultar el rol del usuario usando el UserId
        String role = userService.getUserRoleById(userId, token);
        System.out.println("Rol del usuario: " + role);

            // Verificar si el usuario tiene el rol de "ADMIN"
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permisos para crear cursos.");
        }

            // Si todo es válido, continuar con la creación del curso
        List<Video> videos = videoService.searchVideos(course.getDescription());
        if (videos.isEmpty()) {
            return ResponseEntity.badRequest().body("No se encontraron videos relacionados con el curso.");
        }

            // Guardar el curso
        course.setAdditionalResources(videos);
        Course savedCourse = courseService.createCourse(course);
        return ResponseEntity.ok(savedCourse);

    }


    public ResponseEntity<String> controllerFallback(Throwable throwable) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Fallback Circuit Breaker Activo: " + throwable.getMessage());
    }

    // PUT /api/courses/{id} - Actualiza un curso existente
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@RequestHeader("Authorization") String token, @PathVariable String id, @Valid @RequestBody Course course) {
        try {
            token = token.trim();

            // Extraer el UserId del token
            String userId = userService.extractUserId(token);
            System.out.println("UserId extraído: " + userId);

            // Consultar el rol del usuario usando el UserId
            String role = userService.getUserRoleById(userId, token);
            System.out.println("Rol del usuario: " + role);

            if (!"ADMIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permisos para actualizar cursos.");
            }
            Course updatedCourse = courseService.updateCourse(id, course);
            return ResponseEntity.ok(updatedCourse);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/rating")
    public ResponseEntity<Course> updateRating(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        if (!updates.containsKey("rating")) {
            return ResponseEntity.badRequest().build();
        }

        try {
            double nuevoRating = ((Number) updates.get("rating")).doubleValue();
            if (nuevoRating < 0 || nuevoRating > 10) {
                return ResponseEntity.badRequest().body(null);
            }

            Course cursoActualizado = courseService.actualizarRating(id, nuevoRating);
            if (cursoActualizado == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(cursoActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE /api/courses/{id} - Elimina un curso por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@RequestHeader("Authorization") String token, @PathVariable String id) {
        try {
            token = token.trim();

            // Extraer el UserId del token
            String userId = userService.extractUserId(token);
            System.out.println("UserId extraído: " + userId);

            // Consultar el rol del usuario usando el UserId
            String role = userService.getUserRoleById(userId, token);
            System.out.println("Rol del usuario: " + role);

            if (!"ADMIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permisos para eliminar cursos.");
            }
            courseService.deleteCourse(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/clear-all")
    public ResponseEntity<String> clearAllCache() {
        videoService.clearAllCache();
        return ResponseEntity.ok("Toda la caché ha sido limpiada.");
    }
}
