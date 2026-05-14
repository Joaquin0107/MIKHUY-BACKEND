package pe.MIKHUY.Controllers;

import pe.MIKHUY.Service.OpenAIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = {"http://localhost:4200", "https://mikhuy-front.web.app"})
public class ChatbotController {

    private final OpenAIService openAIService;

    public ChatbotController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    // ── Endpoint principal: texto + imagen si la pide ───────
    @PostMapping("/consulta")
    public Mono<ResponseEntity<Map<String, Object>>> realizarConsulta(
            @RequestBody Map<String, String> request) {

        String pregunta = request.get("pregunta");

        if (pregunta == null || pregunta.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest()
                    .body(Map.of(
                            "error", "La pregunta no puede estar vacía",
                            "tieneImagen", false
                    )));
        }

        if (pregunta.length() > 1000) {
            return Mono.just(ResponseEntity.badRequest()
                    .body(Map.of(
                            "error", "La pregunta es demasiado larga. Por favor, reduce el texto.",
                            "tieneImagen", false
                    )));
        }

        return openAIService.consultarConImagen(pregunta)
                .map(ResponseEntity::ok)
                .onErrorResume(error -> {
                    error.printStackTrace();
                    Map<String, Object> errorMap = new HashMap<>();
                    errorMap.put("error",      "Error al procesar la consulta");
                    errorMap.put("mensaje",    error.getMessage());
                    errorMap.put("tieneImagen", false);
                    return Mono.just(ResponseEntity.internalServerError().body(errorMap));
                });
    }

    // ── Health check ─────────────────────────────────────────
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status",  "OK",
                "service", "Chatbot Nutrición API",
                "model",   "gpt-4o-mini + dall-e-3"
        ));
    }

    // ── Modelos disponibles ───────────────────────────────────
    @GetMapping("/modelos")
    public ResponseEntity<Map<String, Object>> obtenerModelosDisponibles() {
        Map<String, Object> modelos = new HashMap<>();
        modelos.put("modelo_actual", "gpt-4o-mini");
        modelos.put("modelos_disponibles", List.of(
                Map.of(
                        "id",          "gpt-4o-mini",
                        "nombre",      "GPT-4o Mini",
                        "velocidad",   "Alta",
                        "costo",       "Muy económico",
                        "descripcion", "Ideal para chatbots y respuestas rápidas"
                ),
                Map.of(
                        "id",          "gpt-4o",
                        "nombre",      "GPT-4o",
                        "velocidad",   "Media",
                        "costo",       "Moderado",
                        "descripcion", "Más potente, para tareas complejas"
                ),
                Map.of(
                        "id",          "gpt-3.5-turbo",
                        "nombre",      "GPT-3.5 Turbo",
                        "velocidad",   "Muy alta",
                        "costo",       "Bajo",
                        "descripcion", "Rápido y económico"
                )
        ));
        return ResponseEntity.ok(modelos);
    }
}