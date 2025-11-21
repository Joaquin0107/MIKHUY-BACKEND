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
     */
    public Mono<String> consultarNutricion(String preguntaUsuario) {

        // Prompt nutricional
        String prompt =
                "Eres un asistente experto en nutrición, dietas y alimentación saludable. " +
                        "Responde SIEMPRE basado en evidencia científica, con un tono claro y profesional. " +
                        "Si la pregunta no es sobre nutrición, responde: 'Solo puedo responder temas de nutrición'.\n\n" +
                        "Pregunta: " + preguntaUsuario;

        // Request body CORRECTO para /v1/responses con GPT-5.1
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-5.1",
                "input", prompt,
                "reasoning_effort", "none" // Para respuestas rápidas sin razonamiento profundo
        );

        return webClient.post()
                .uri("https://api.openai.com/v1/responses")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    System.err.println("Error de OpenAI: " + errorBody);
                                    return Mono.error(new RuntimeException("Error de OpenAI: " + errorBody));
                                })
                )
                .bodyToMono(JsonNode.class)
                .doOnNext(res -> {
                    System.out.println("===== RAW RESPONSE FROM OPENAI =====");
                    System.out.println(res.toPrettyString());
                })
                .map(response -> {
                    try {
                        // Para /v1/responses, la estructura es: output -> [array] -> content
                        JsonNode output = response.path("output");

                        if (output.isArray() && output.size() > 0) {
                            JsonNode firstOutput = output.get(0);

                            // Puede ser texto directo o dentro de "content"
                            if (firstOutput.has("text")) {
                                return firstOutput.path("text").asText();
                            } else if (firstOutput.has("content")) {
                                JsonNode content = firstOutput.path("content");
                                if (content.isArray() && content.size() > 0) {
                                    return content.get(0).path("text").asText();
                                } else if (content.isTextual()) {
                                    return content.asText();
                                }
                            } else if (firstOutput.isTextual()) {
                                return firstOutput.asText();
                            }
                        }

                        // Si no tiene "output", puede tener "output_text" directamente
                        if (response.has("output_text")) {
                            return response.path("output_text").asText();
                        }

                        return "No se pudo leer la respuesta del modelo.";

                    } catch (Exception e) {
                        System.err.println("Error procesando respuesta:");
                        e.printStackTrace();
                        return "Error al procesar la respuesta de OpenAI.";
                    }
                })
                .onErrorResume(error -> {
                    System.err.println("Error llamando a OpenAI: " + error.getMessage());
                    error.printStackTrace();
                    return Mono.just("Error: No se pudo conectar con el servicio de IA. " +
                            "Verifica tu API key y que tienes acceso al modelo GPT-5.1");
                });
    }
}