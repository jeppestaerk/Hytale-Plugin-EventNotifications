package dev.valhal.hytale.plugin.config.validation;

/**
 * Exception thrown when configuration validation fails.
 */
public class ConfigValidationException extends RuntimeException {
    private final ConfigValidationResult result;

    public ConfigValidationException(ConfigValidationResult result) {
        super(buildMessage(result));
        this.result = result;
    }

    public ConfigValidationException(String message) {
        super(message);
        this.result = new ConfigValidationResult();
        this.result.addError(message);
    }

    public ConfigValidationResult getResult() {
        return result;
    }

    private static String buildMessage(ConfigValidationResult result) {
        if (result.getErrors().isEmpty()) {
            return "Configuration validation failed";
        }
        if (result.getErrors().size() == 1) {
            return result.getErrors().getFirst();
        }
        return String.format("Configuration validation failed with %d errors: %s",
            result.getErrors().size(),
            String.join("; ", result.getErrors()));
    }
}
