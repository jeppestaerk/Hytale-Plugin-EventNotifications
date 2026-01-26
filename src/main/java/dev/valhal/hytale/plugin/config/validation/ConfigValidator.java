package dev.valhal.hytale.plugin.config.validation;

import dev.valhal.hytale.plugin.config.EventConfig;
import dev.valhal.hytale.plugin.config.EventType;
import dev.valhal.hytale.plugin.config.PluginConfig;
import dev.valhal.hytale.plugin.config.TargetConfig;

import java.util.Map;

/**
 * Validates plugin configuration and reports errors/warnings.
 */
public class ConfigValidator {

    /**
     * Validates the entire plugin configuration.
     *
     * @param config the configuration to validate
     * @return validation result with errors and warnings
     */
    public ConfigValidationResult validate(PluginConfig config) {
        ConfigValidationResult result = new ConfigValidationResult();

        if (config == null) {
            result.addError("Configuration is null");
            return result;
        }

        Map<String, TargetConfig> targets = config.getTargets();
        if (targets == null || targets.isEmpty()) {
            result.addWarning("No notification targets configured");
            return result;
        }

        for (Map.Entry<String, TargetConfig> entry : targets.entrySet()) {
            String targetName = entry.getKey();
            TargetConfig targetConfig = entry.getValue();
            result.merge(validateTarget(targetName, targetConfig));
        }

        return result;
    }

    /**
     * Validates a single target configuration.
     */
    public ConfigValidationResult validateTarget(String name, TargetConfig config) {
        ConfigValidationResult result = new ConfigValidationResult();

        if (config == null) {
            result.addError("Target '%s': configuration is null", name);
            return result;
        }

        // Validate type
        String type = config.getType();
        if (type == null || type.isEmpty()) {
            result.addError("Target '%s': missing 'type' field", name);
        }

        // Validate URL
        if (config.isEnabled()) {
            String url = config.getUrl();
            if (url == null || url.isEmpty()) {
                result.addError("Target '%s': missing 'url' field", name);
            } else if (!url.startsWith("http://") && !url.startsWith("https://")) {
                result.addError("Target '%s': URL must start with http:// or https://", name);
            }
        }

        // Validate events
        Map<String, EventConfig> events = config.getEvents();
        if (events == null || events.isEmpty()) {
            result.addWarning("Target '%s': no events configured", name);
        } else {
            for (Map.Entry<String, EventConfig> eventEntry : events.entrySet()) {
                String eventKey = eventEntry.getKey();
                if (!EventType.isValid(eventKey)) {
                    result.addError("Target '%s': unknown event type '%s'. Valid types: %s",
                        name, eventKey, getValidEventTypes());
                }
            }
        }

        return result;
    }

    private String getValidEventTypes() {
        StringBuilder sb = new StringBuilder();
        EventType[] types = EventType.values();
        for (int i = 0; i < types.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(types[i].getConfigKey());
        }
        return sb.toString();
    }

    /**
     * Validates configuration and throws exception if invalid.
     *
     * @param config the configuration to validate
     * @throws ConfigValidationException if validation fails
     */
    public void validateOrThrow(PluginConfig config) {
        ConfigValidationResult result = validate(config);
        if (result.hasErrors()) {
            throw new ConfigValidationException(result);
        }
    }
}
