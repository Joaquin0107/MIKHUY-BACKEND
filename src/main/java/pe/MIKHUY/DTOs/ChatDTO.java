package pe.MIKHUY.DTOs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

// ────────────────────────────────────────────
// DTO Request — lo que llega del frontend
// ────────────────────────────────────────────
public class ChatDTO {
    private String pregunta;

    public ChatDTO() {}

    public ChatDTO(String pregunta) {
        this.pregunta = pregunta;
    }

    public String getPregunta() { return pregunta; }
    public void setPregunta(String pregunta) { this.pregunta = pregunta; }
}

// ────────────────────────────────────────────
// DTO Response — lo que se devuelve al frontend
// Incluye imagenUrl y tieneImagen (nuevos campos)
// ────────────────────────────────────────────
@JsonInclude(JsonInclude.Include.NON_NULL)
class ChatResponse {
    private String  respuesta;
    private String  imagenUrl;   // URL de DALL-E (vacío si no pidió imagen)
    private Boolean tieneImagen; // true/false para que Angular sepa si mostrar img
    private Long    timestamp;
    private String  modelo;
    private UsageInfo usage;

    public ChatResponse() {}

    public ChatResponse(String respuesta, String imagenUrl,
                        Boolean tieneImagen, Long timestamp, String modelo) {
        this.respuesta   = respuesta;
        this.imagenUrl   = imagenUrl;
        this.tieneImagen = tieneImagen;
        this.timestamp   = timestamp;
        this.modelo      = modelo;
    }

    public String  getRespuesta()   { return respuesta; }
    public String  getImagenUrl()   { return imagenUrl; }
    public Boolean getTieneImagen() { return tieneImagen; }
    public Long    getTimestamp()   { return timestamp; }
    public String  getModelo()      { return modelo; }
    public UsageInfo getUsage()     { return usage; }

    public void setRespuesta(String respuesta)     { this.respuesta   = respuesta; }
    public void setImagenUrl(String imagenUrl)     { this.imagenUrl   = imagenUrl; }
    public void setTieneImagen(Boolean tieneImagen){ this.tieneImagen = tieneImagen; }
    public void setTimestamp(Long timestamp)       { this.timestamp   = timestamp; }
    public void setModelo(String modelo)           { this.modelo      = modelo; }
    public void setUsage(UsageInfo usage)          { this.usage       = usage; }
}

// ────────────────────────────────────────────
// DTO Tokens — información de uso de OpenAI
// ────────────────────────────────────────────
class UsageInfo {
    @JsonProperty("prompt_tokens")
    private Integer promptTokens;

    @JsonProperty("completion_tokens")
    private Integer completionTokens;

    @JsonProperty("total_tokens")
    private Integer totalTokens;

    public UsageInfo() {}

    public UsageInfo(Integer promptTokens, Integer completionTokens, Integer totalTokens) {
        this.promptTokens     = promptTokens;
        this.completionTokens = completionTokens;
        this.totalTokens      = totalTokens;
    }

    public Integer getPromptTokens()     { return promptTokens; }
    public Integer getCompletionTokens() { return completionTokens; }
    public Integer getTotalTokens()      { return totalTokens; }

    public void setPromptTokens(Integer promptTokens)         { this.promptTokens     = promptTokens; }
    public void setCompletionTokens(Integer completionTokens) { this.completionTokens = completionTokens; }
    public void setTotalTokens(Integer totalTokens)           { this.totalTokens      = totalTokens; }
}

// ────────────────────────────────────────────
// DTO Mensaje OpenAI — rol + contenido
// ────────────────────────────────────────────
class OpenAIMessage {
    private String role;
    private String content;

    public OpenAIMessage() {}

    public OpenAIMessage(String role, String content) {
        this.role    = role;
        this.content = content;
    }

    public String getRole()    { return role; }
    public String getContent() { return content; }

    public void setRole(String role)       { this.role    = role; }
    public void setContent(String content) { this.content = content; }
}