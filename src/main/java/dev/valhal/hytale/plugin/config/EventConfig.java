package dev.valhal.hytale.plugin.config;

import java.util.Map;

/**
 * Configuration for a specific event within a target.
 */
public class EventConfig {
    private boolean enabled = true;
    private String title;
    private String message;
    private String priority;      // ntfy priority
    private String tags;          // ntfy tags
    private String bodyTemplate;  // Override for webhook
    private String color;         // Discord embed color (hex)
    private String icon;          // ntfy icon URL

    public boolean isEnabled() {
        return enabled;
    }

    public String getTitle() {
        return title != null ? title : "";
    }

    public String getMessage() {
        return message != null ? message : "";
    }

    public String getPriority() {
        return priority != null ? priority : "";
    }

    public String getTags() {
        return tags != null ? tags : "";
    }

    public String getBodyTemplate() {
        return bodyTemplate != null ? bodyTemplate : "";
    }

    public String getColor() {
        return color != null ? color : "";
    }

    public String getIcon() {
        return icon != null ? icon : "";
    }

    /**
     * Apply placeholders to a template string.
     */
    public String format(String template, Map<String, String> data) {
        if (template == null || template.isEmpty()) {
            return "";
        }
        String result = template;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }

    public String formatTitle(Map<String, String> data) {
        return format(title, data);
    }

    public String formatMessage(Map<String, String> data) {
        return format(message, data);
    }

    public String formatBodyTemplate(Map<String, String> data) {
        return format(bodyTemplate, data);
    }
}
