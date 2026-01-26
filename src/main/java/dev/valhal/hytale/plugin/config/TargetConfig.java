package dev.valhal.hytale.plugin.config;

import java.util.Map;

/**
 * Base configuration for a notification target.
 */
public class TargetConfig {
    private String type;
    private boolean enabled = true;
    private String url;

    // Auth options (only one should be used)
    private String username;
    private String password;
    private String bearerToken;

    // ntfy-specific options
    private Boolean markdown;
    private String defaultPriority;
    private String defaultIcon;

    // Generic webhook options
    private String contentType;
    private Map<String, String> headers;
    private String bodyTemplate;

    // Discord-specific options
    private String discordUsername;
    private String discordAvatarUrl;
    private Boolean discordUseEmbeds;

    // Per-target event configurations
    private Map<String, EventConfig> events;

    public String getType() {
        return type != null ? type : "";
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getUrl() {
        return url != null ? url : "";
    }

    public String getUsername() {
        return username != null ? username : "";
    }

    public String getPassword() {
        return password != null ? password : "";
    }

    public String getBearerToken() {
        return bearerToken != null ? bearerToken : "";
    }

    public boolean isMarkdown() {
        return markdown != null ? markdown : true;
    }

    public String getDefaultPriority() {
        return defaultPriority != null ? defaultPriority : "default";
    }

    public String getDefaultIcon() {
        return defaultIcon != null ? defaultIcon : "";
    }

    public String getContentType() {
        return contentType != null ? contentType : "application/json";
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBodyTemplate() {
        return bodyTemplate != null ? bodyTemplate : "";
    }

    public String getDiscordUsername() {
        return discordUsername != null ? discordUsername : "";
    }

    public String getDiscordAvatarUrl() {
        return discordAvatarUrl != null ? discordAvatarUrl : "";
    }

    public boolean isDiscordUseEmbeds() {
        return discordUseEmbeds != null ? discordUseEmbeds : true;
    }

    public Map<String, EventConfig> getEvents() {
        return events;
    }

    public EventConfig getEvent(EventType eventType) {
        return events != null ? events.get(eventType.getConfigKey()) : null;
    }

    public boolean isEventEnabled(EventType eventType) {
        EventConfig event = getEvent(eventType);
        return event != null && event.isEnabled();
    }

    public boolean hasBasicAuth() {
        return username != null && !username.isEmpty()
            && password != null && !password.isEmpty();
    }

    public boolean hasBearerAuth() {
        return bearerToken != null && !bearerToken.isEmpty();
    }
}
