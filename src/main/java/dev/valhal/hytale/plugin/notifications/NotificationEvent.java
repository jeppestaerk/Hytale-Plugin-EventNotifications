package dev.valhal.hytale.plugin.notifications;

import dev.valhal.hytale.plugin.config.EventType;

import java.util.Map;

/**
 * Raw notification event with placeholder data.
 * Each target formats this according to its own configuration.
 */
public record NotificationEvent(
    EventType eventType,
    Map<String, String> data
) {
    public String get(String key) {
        return data.getOrDefault(key, "");
    }

    public String get(String key, String defaultValue) {
        String value = data.get(key);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }
}
