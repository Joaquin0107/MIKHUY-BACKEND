package pe.MIKHUY.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

        // Request body para /v1/responses
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-5.1",
                "input", prompt
        );

        return webClient.post()
                .uri("https://api.openai.com/v1/responses")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .header("OpenAI-Beta", "max-output-tokens=2048") // NECESARIO PARA GPT-5
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(response -> {
                    try {
                        // Nuevo formato oficial del endpoint /responses
                        JsonNode outputArray = response.get("output_text");

                        if (outputArray != null && outputArray.isArray() && outputArray.size() > 0) {
                            return outputArray.get(0).asText();
                        }

                        return "No se pudo leer la respuesta del modelo.";
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "Error al procesar la respuesta de OpenAI.";
                    }
                })
                .onErrorResume(error -> {
                    System.err.println("Error OpenAI /responses: " + error.getMessage());
                    return Mono.just("Hubo un problema al consultar la IA. Intenta más tarde.");
                });
    }

}
