package dev.valhal.hytale.plugin.config.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result of configuration validation containing errors and warnings.
 */
public class ConfigValidationResult {
    private final List<String> errors;
    private final List<String> warnings;

    public ConfigValidationResult() {
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }

    public void addError(String message) {
        errors.add(message);
    }

    public void addError(String format, Object... args) {
        errors.add(String.format(format, args));
    }

    public void addWarning(String message) {
        warnings.add(message);
    }

    public void addWarning(String format, Object... args) {
        warnings.add(String.format(format, args));
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    public boolean isValid() {
        return !hasErrors();
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public List<String> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }

    public void merge(ConfigValidationResult other) {
        this.errors.addAll(other.errors);
        this.warnings.addAll(other.warnings);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (hasErrors()) {
            sb.append("Errors:\n");
            for (String error : errors) {
                sb.append("  - ").append(error).append("\n");
            }
        }
        if (hasWarnings()) {
            sb.append("Warnings:\n");
            for (String warning : warnings) {
                sb.append("  - ").append(warning).append("\n");
            }
        }
        if (!hasErrors() && !hasWarnings()) {
            sb.append("Configuration is valid.");
        }
        return sb.toString();
    }
}
