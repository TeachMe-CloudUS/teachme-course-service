package us.cloud.teachme.courseservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class UserService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    public String getUserRoleById(String userId) {
        String url = "http://MICROAUTHENTICATION-SERVICE/api/v1/user/" + userId;
        JsonNode userNode = webClientBuilder.build()
            .get()
            .uri(url)
            .retrieve()
            .bodyToMono(JsonNode.class) // Recupera la respuesta como JSON
            .block(); // Bloquea para obtener la respuesta sincrónicamente (puedes evitarlo en entornos reactivos)

        return userNode.get("role").asText(); // Extrae el rol
    }

    public String extractUserId(String token) {
        Claims claims = Jwts.parserBuilder()
            .build()
            .parseClaimsJwt(token) // No valida la firma, solo decodifica
            .getBody();
    
        return claims.getSubject(); // Devuelve el ID del usuario
    }
    
}
