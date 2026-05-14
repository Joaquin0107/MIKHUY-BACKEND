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

    // ────────────────────────────────────────────
    // 1. CONSEJO NUTRICIONAL → GPT-4o-mini
    // ────────────────────────────────────────────
    public Mono<String> consultarNutricion(String preguntaUsuario) {

        String systemPrompt =
                "Eres un asistente experto en nutrición, dietas y alimentación saludable. " +
                        "Responde SIEMPRE basado en evidencia científica, con un tono claro y profesional. " +
                        "Si el usuario pide crear o generar una imagen, describe brevemente el alimento " +
                        "o plato mencionado de forma nutricional. " +
                        "Si la pregunta no es sobre nutrición ni alimentación, responde exactamente: " +
                        "'Solo puedo responder temas de nutrición y alimentación saludable.'";

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
                .doOnNext(res -> System.out.println("✅ GPT response: " + res.toPrettyString()))
                .map(response -> {
                    JsonNode choices = response.path("choices");
                    if (choices.isArray() && choices.size() > 0) {
                        JsonNode content = choices.get(0).path("message").path("content");
                        if (!content.isMissingNode() && !content.isNull()) {
                            return content.asText();
                        }
                    }
                    return "No se pudo obtener una respuesta válida del modelo.";
                })
                .onErrorResume(error -> Mono.just(manejarError(error)));
    }

    // ────────────────────────────────────────────
    // 2. EXTRAER TEMA VISUAL → GPT-4o-mini
    //    Convierte la pregunta en palabras clave
    //    limpias para pasarle a DALL-E
    // ────────────────────────────────────────────
    private Mono<String> extraerTemaVisual(String preguntaUsuario) {

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(
                        Map.of("role", "system", "content",
                                "Eres un asistente que extrae el tema visual de un mensaje sobre nutrición. " +
                                        "Responde SOLO con 3-5 palabras en inglés que describan el alimento o plato " +
                                        "más representativo del mensaje. Sin explicaciones, sin puntuación, solo las palabras."),
                        Map.of("role", "user", "content", preguntaUsuario)
                ),
                "max_tokens", 20,
                "temperature", 0.3
        );

        return webClient.post()
                .uri("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(response -> {
                    JsonNode choices = response.path("choices");
                    if (choices.isArray() && choices.size() > 0) {
                        return choices.get(0).path("message").path("content")
                                .asText("healthy nutritious food");
                    }
                    return "healthy nutritious food";
                })
                .onErrorReturn("healthy nutritious food");
    }

    // ────────────────────────────────────────────
    // 3. GENERAR IMAGEN → DALL-E 3
    // ────────────────────────────────────────────
    private Mono<String> generarImagenNutricional(String temaVisual) {

        String promptImagen =
                "Professional food photography of " + temaVisual + ". " +
                        "Fresh, healthy, vibrant colors, white background, " +
                        "high resolution, appetizing presentation, natural lighting, " +
                        "no text, no labels.";

        Map<String, Object> requestBody = Map.of(
                "model",   "dall-e-3",
                "prompt",  promptImagen,
                "n",       1,
                "size",    "1024x1024",
                "quality", "standard",
                "style",   "natural"
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
                    JsonNode data = response.path("data");
                    if (data.isArray() && data.size() > 0) {
                        JsonNode url = data.get(0).path("url");
                        if (!url.isMissingNode() && !url.isNull()) {
                            return url.asText();
                        }
                    }
                    return "";
                })
                .onErrorResume(error -> {
                    System.err.println("❌ Error generando imagen: " + error.getMessage());
                    return Mono.just("");
                });
    }

    // ────────────────────────────────────────────
    // 4. DETECTAR SI EL USUARIO PIDE IMAGEN
    // ────────────────────────────────────────────
    private boolean solicitaImagen(String pregunta) {
        String lower = pregunta.toLowerCase();
        return lower.contains("crea imagen") ||
                lower.contains("crea una imagen") ||
                lower.contains("dame imagen") ||
                lower.contains("dame una imagen") ||
                lower.contains("genera imagen") ||
                lower.contains("genera una imagen") ||
                lower.contains("muéstrame imagen") ||
                lower.contains("muestrame imagen") ||
                lower.contains("quiero imagen") ||
                lower.contains("quiero una imagen") ||
                lower.contains("imagen de") ||
                lower.contains("foto de") ||
                lower.contains("mostrar imagen");
    }

    // ────────────────────────────────────────────
    // 5. MÉTODO PRINCIPAL — texto + imagen opcional
    //    Solo genera imagen si el usuario lo pide
    // ────────────────────────────────────────────
    public Mono<Map<String, Object>> consultarConImagen(String preguntaUsuario) {

        boolean pidioImagen = solicitaImagen(preguntaUsuario);

        if (pidioImagen) {
            // Ejecuta texto e imagen en paralelo
            return Mono.zip(
                    consultarNutricion(preguntaUsuario),
                    extraerTemaVisual(preguntaUsuario)
                            .flatMap(this::generarImagenNutricional)
            ).map(tuple -> Map.of(
                    "respuesta",  (Object) tuple.getT1(),
                    "imagenUrl",  tuple.getT2() != null ? tuple.getT2() : "",
                    "tieneImagen", !tuple.getT2().isEmpty(),
                    "timestamp",  System.currentTimeMillis(),
                    "modelo",     "gpt-4o-mini + dall-e-3"
            ));
        }

        // Solo texto
        return consultarNutricion(preguntaUsuario)
                .map(consejo -> Map.of(
                        "respuesta",   (Object) consejo,
                        "imagenUrl",   "",
                        "tieneImagen", false,
                        "timestamp",   System.currentTimeMillis(),
                        "modelo",      "gpt-4o-mini"
                ));
    }

    // ────────────────────────────────────────────
    // Helper: manejo de errores comunes
    // ────────────────────────────────────────────
    private String manejarError(Throwable error) {
        System.err.println("❌ Error: " + error.getMessage());
        String msg = error.getMessage();
        if (msg != null) {
            if (msg.contains("401"))                return "Error: API key inválida o expirada.";
            if (msg.contains("429"))                return "Error: Límite de requests alcanzado. Intenta más tarde.";
            if (msg.contains("insufficient_quota")) return "Error: Tu cuenta de OpenAI no tiene créditos disponibles.";
            if (msg.contains("content_policy"))     return "Error: Contenido rechazado por políticas de OpenAI.";
        }
        return "Error al conectar con OpenAI. Verifica tu configuración.";
    }
}