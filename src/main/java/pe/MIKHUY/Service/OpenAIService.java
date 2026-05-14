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

    // ─────────────────────────────────────────────
    // 1. CONSEJO NUTRICIONAL  →  GPT-4o-mini
    // ─────────────────────────────────────────────
    public Mono<String> consultarNutricion(String preguntaUsuario) {

        String systemPrompt =
                "Eres un asistente experto en nutrición, dietas y alimentación saludable. " +
                        "Responde SIEMPRE basado en evidencia científica, con un tono claro y profesional. " +
                        "Si la pregunta no es sobre nutrición, responde: 'Solo puedo responder temas de nutrición'.";

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user",   "content", preguntaUsuario)
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
                                .flatMap(err -> {
                                    System.err.println("❌ Error GPT: " + err);
                                    return Mono.error(new RuntimeException("Error GPT: " + err));
                                })
                )
                .bodyToMono(JsonNode.class)
                .map(response -> {
                    JsonNode choices = response.path("choices");
                    if (choices.isArray() && !choices.isEmpty()) {
                        JsonNode content = choices.get(0).path("message").path("content");
                        if (!content.isMissingNode() && !content.isNull()) {
                            return content.asText();
                        }
                    }
                    return "No se pudo obtener una respuesta válida del modelo.";
                })
                .onErrorResume(error -> Mono.just(manejarError(error)));
    }

    // ─────────────────────────────────────────────
    // 2. GENERACIÓN DE IMAGEN  →  DALL-E 3
    // ─────────────────────────────────────────────
    public Mono<String> generarImagenNutricional(String temaAlimentario) {

        // Prompt optimizado para imágenes de alimentación saludable
        String promptImagen = "Professional food photography of " + temaAlimentario +
                ". Healthy, fresh, vibrant colors, white background, " +
                "high resolution, appetizing presentation, natural lighting.";

        Map<String, Object> requestBody = Map.of(
                "model",   "dall-e-3",
                "prompt",  promptImagen,
                "n",       1,                // DALL-E 3 solo permite n=1
                "size",    "1024x1024",      // Opciones: 1024x1024 | 1792x1024 | 1024x1792
                "quality", "standard",       // "standard" o "hd" (hd cuesta más créditos)
                "style",   "natural"         // "natural" o "vivid"
        );

        return webClient.post()
                .uri("https://api.openai.com/v1/images/generations")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(err -> {
                                    System.err.println("❌ Error DALL-E: " + err);
                                    return Mono.error(new RuntimeException("Error DALL-E: " + err));
                                })
                )
                .bodyToMono(JsonNode.class)
                .doOnNext(res -> System.out.println("✅ DALL-E response: " + res.toPrettyString()))
                .map(response -> {
                    // Estructura: data[0].url
                    JsonNode data = response.path("data");
                    if (data.isArray() && !data.isEmpty()) {
                        JsonNode url = data.get(0).path("url");
                        if (!url.isMissingNode() && !url.isNull()) {
                            return url.asText();
                        }
                    }
                    return "No se pudo generar la imagen.";
                })
                .onErrorResume(error -> Mono.just(manejarError(error)));
    }

    // ─────────────────────────────────────────────
    // 3. CONSEJO + IMAGEN  (método combinado útil)
    // ─────────────────────────────────────────────
    public Mono<Map<String, String>> consultarConImagen(String preguntaUsuario) {
        return Mono.zip(
                consultarNutricion(preguntaUsuario),
                generarImagenNutricional(preguntaUsuario)
        ).map(tuple -> Map.of(
                "consejo",   tuple.getT1(),
                "imagenUrl", tuple.getT2()
        ));
    }

    // ─────────────────────────────────────────────
    // Helper privado de errores
    // ─────────────────────────────────────────────
    private String manejarError(Throwable error) {
        System.err.println("❌ Error: " + error.getMessage());
        String msg = error.getMessage();
        if (msg != null) {
            if (msg.contains("401"))                  return "Error: API key inválida o expirada.";
            if (msg.contains("429"))                  return "Error: Límite de requests alcanzado. Intenta más tarde.";
            if (msg.contains("insufficient_quota"))   return "Error: Tu cuenta de OpenAI no tiene créditos disponibles.";
            if (msg.contains("billing"))              return "Error: Revisa tu plan de facturación en OpenAI.";
            if (msg.contains("content_policy"))       return "Error: El contenido fue rechazado por las políticas de OpenAI.";
        }
        return "Error al conectar con OpenAI. Verifica tu configuración.";
    }
}