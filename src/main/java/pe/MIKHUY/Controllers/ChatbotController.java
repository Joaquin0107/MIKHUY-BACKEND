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

    @PostMapping("/consulta")
    public Mono<ResponseEntity<Map<String, Object>>> realizarConsulta(
            @RequestBody Map<String, String> request) {

        String pregunta = request.get("pregunta");

        // Validación: no vacío
        if (pregunta == null || pregunta.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest()
                    .body(Map.of("error", "La pregunta no puede estar vacía")));
        }

        // Validación: tamaño
        if (pregunta.length() > 1000) {
            return Mono.just(ResponseEntity.badRequest()
                    .body(Map.of("error", "La pregunta es demasiado larga. " +
                            "Por favor, reduce el texto.")));
        }

        return openAIService.consultarNutricion(pregunta)
                .map(respuesta -> {
                    Map<String, Object> responseMap = new HashMap<>();
                    responseMap.put("respuesta", respuesta);
                    responseMap.put("timestamp", System.currentTimeMillis());
                    responseMap.put("modelo", "gpt-4o-mini"); // ✅ Modelo real
                    return ResponseEntity.ok(responseMap);
                })
                .onErrorResume(error -> {
                    error.printStackTrace();
                    Map<String, Object> errorMap = new HashMap<>();
                    errorMap.put("error", "Error al procesar la consulta");
                    errorMap.put("mensaje", error.getMessage());
                    return Mono.just(ResponseEntity.internalServerError().body(errorMap));
                });
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "OK",
                "service", "Chatbot API",
                "model", "gpt-4o-mini"
        ));
    }

    @GetMapping("/modelos")
    public ResponseEntity<Map<String, Object>> obtenerModelosDisponibles() {

        Map<String, Object> modelos = new HashMap<>();
        modelos.put("modelo_actual", "gpt-4o-mini");

        modelos.put("modelos_disponibles", List.of(
                Map.of(
                        "id", "gpt-4o-mini",
                        "nombre", "GPT-4o Mini",
                        "velocidad", "Alta",
                        "costo", "Muy económico",
                        "descripcion", "Ideal para chatbots y respuestas rápidas"
                ),
                Map.of(
                        "id", "gpt-4o",
                        "nombre", "GPT-4o",
                        "velocidad", "Media",
                        "costo", "Moderado",
                        "descripcion", "Más potente, para tareas complejas"
                ),
                Map.of(
                        "id", "gpt-3.5-turbo",
                        "nombre", "GPT-3.5 Turbo",
                        "velocidad", "Muy alta",
                        "costo", "Bajo",
                        "descripcion", "Rápido y económico"
                )
        ));

        return ResponseEntity.ok(modelos);
    }
}