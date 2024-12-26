package us.cloud.teachme.courseservice.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import us.cloud.teachme.courseservice.model.Video;

@Service
public class VideoService {
    @Autowired
    private final WebClient webClient;

    @Value("${youtube.api.key}")
    private String apiKey;

    @Autowired
    public VideoService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://www.googleapis.com/youtube/v3").build();
    }

    @Cacheable(value = "videosCache", key = "#query")
    public List<Video> searchVideos(String query) {
        String uri = String.format("/search?part=snippet&q=%s&type=video&key=%s&maxResults=5", query, apiKey);
        // String uri = String.format("/uri-pocha", query, apiKey);

        Map<String, Object> response = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block();

        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
        return items.stream()
                .map(item -> {
                    Map<String, Object> snippet = (Map<String, Object>) item.get("snippet");
                    Map<String, Object> id = (Map<String, Object>) item.get("id");
                    String videoId = (String) id.get("videoId");

                    Video video = new Video();
                    video.setTitle((String) snippet.get("title"));
                    video.setDescription((String) snippet.get("description"));
                    video.setUrl("https://www.youtube.com/watch?v=" + videoId);
                    return video;
                })
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "videosCache", key = "#query")
    public void clearCache(String query) {
        // Limpia la caché para la consulta específica
    }

    @CacheEvict(value = "videosCache", allEntries = true)
    public void clearAllCache() {
        // Limpia toda la caché
    }
}
