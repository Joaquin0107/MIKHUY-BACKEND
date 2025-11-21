package pe.MIKHUY.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public OpenAIService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    /**
     * Método principal: solo responde sobre nutrición.
     * Usa el endpoint estándar /v1/chat/completions que funciona con cualquier API key
     */
    public Mono<String> consultarNutricion(String preguntaUsuario) {

        // Prompt nutricional
        String systemPrompt =
                "Eres un asistente experto en nutrición, dietas y alimentación saludable. " +
                        "Responde SIEMPRE basado en evidencia científica, con un tono claro y profesional. " +
                        "Si la pregunta no es sobre nutrición, responde: 'Solo puedo responder temas de nutrición'.";

        // Request body para /v1/chat/completions (endpoint UNIVERSAL)
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o-mini", // Modelo que SÍ funciona con todas las API keys
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", preguntaUsuario)
                ),
                "max_tokens", 800,
                "temperature", 0.7
        );

        return webClient.post()
                .uri("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    System.err.println("❌ Error de OpenAI: " + errorBody);
                                    return Mono.error(new RuntimeException("Error de OpenAI: " + errorBody));
                                })
                )
                .bodyToMono(JsonNode.class)
                .doOnNext(res -> {
                    System.out.println("✅ RAW RESPONSE FROM OPENAI:");
                    System.out.println(res.toPrettyString());
                })
                .map(response -> {
                    try {
                        // Estructura: choices[0].message.content
                        JsonNode choices = response.path("choices");

                        if (choices.isArray() && choices.size() > 0) {
                            JsonNode message = choices.get(0).path("message");
                            JsonNode content = message.path("content");

                            if (!content.isMissingNode() && !content.isNull()) {
                                return content.asText();
                            }
                        }

                        System.err.println("⚠️ Respuesta inesperada de OpenAI");
                        return "No se pudo obtener una respuesta válida del modelo.";

                    } catch (Exception e) {
                        System.err.println("❌ Error procesando respuesta:");
                        e.printStackTrace();
                        return "Error al procesar la respuesta de OpenAI.";
                    }
                })
                .onErrorResume(error -> {
                    System.err.println("❌ Error completo: " + error.getMessage());
                    error.printStackTrace();

                    String mensaje = error.getMessage();
                    if (mensaje != null && mensaje.contains("401")) {
                        return Mono.just("Error: API key inválida o expirada.");
                    } else if (mensaje != null && mensaje.contains("429")) {
                        return Mono.just("Error: Límite de requests alcanzado. Intenta más tarde.");
                    } else if (mensaje != null && mensaje.contains("insufficient_quota")) {
                        return Mono.just("Error: Tu cuenta de OpenAI no tiene créditos disponibles.");
                    }

                    return Mono.just("Error al conectar con OpenAI. Verifica tu configuración.");
                });
    }
}