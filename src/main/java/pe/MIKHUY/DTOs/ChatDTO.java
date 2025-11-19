package pe.MIKHUY.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

// DTO para la solicitud
public class ChatDTO {
    private String pregunta;

    public ChatDTO() {}

    public ChatDTO(String pregunta) {
        this.pregunta = pregunta;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }
}

// DTO para la respuesta
class ChatResponse {
    private String respuesta;
    private Long timestamp;
    private String modelo;
    private UsageInfo usage;

    public ChatResponse() {}

    public ChatResponse(String respuesta, Long timestamp, String modelo) {
        this.respuesta = respuesta;
        this.timestamp = timestamp;
        this.modelo = modelo;
    }

    // Getters y Setters
    public String getRespuesta() { return respuesta; }
    public void setRespuesta(String respuesta) { this.respuesta = respuesta; }

    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public UsageInfo getUsage() { return usage; }
    public void setUsage(UsageInfo usage) { this.usage = usage; }
}

// DTO para información de uso de tokens
class UsageInfo {
    @JsonProperty("prompt_tokens")
    private Integer promptTokens;

    @JsonProperty("completion_tokens")
    private Integer completionTokens;

    @JsonProperty("total_tokens")
    private Integer totalTokens;

    public UsageInfo() {}

    public UsageInfo(Integer promptTokens, Integer completionTokens, Integer totalTokens) {
        this.promptTokens = promptTokens;
        this.completionTokens = completionTokens;
        this.totalTokens = totalTokens;
    }

    // Getters y Setters
    public Integer getPromptTokens() { return promptTokens; }
    public void setPromptTokens(Integer promptTokens) { this.promptTokens = promptTokens; }

    public Integer getCompletionTokens() { return completionTokens; }
    public void setCompletionTokens(Integer completionTokens) {
        this.completionTokens = completionTokens;
    }

    public Integer getTotalTokens() { return totalTokens; }
    public void setTotalTokens(Integer totalTokens) { this.totalTokens = totalTokens; }
}

// DTO para mensajes de OpenAI
class OpenAIMessage {
    private String role;
    private String content;

    public OpenAIMessage() {}

    public OpenAIMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}