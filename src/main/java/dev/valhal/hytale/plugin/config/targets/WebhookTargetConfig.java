package dev.valhal.hytale.plugin.config.targets;

import dev.valhal.hytale.plugin.config.TargetConfig;

import java.util.Map;

/**
 * Configuration specific to generic webhook targets.
 * Provides typed accessors for webhook-specific options.
 */
public class WebhookTargetConfig {
    public static final String TYPE = "webhook";

    private final TargetConfig config;

    public WebhookTargetConfig(TargetConfig config) {
        this.config = config;
    }

    public TargetConfig getBaseConfig() {
        return config;
    }

    public String getContentType() {
        return config.getContentType();
    }

    public Map<String, String> getHeaders() {
        return config.getHeaders();
    }

    public String getBodyTemplate() {
        return config.getBodyTemplate();
    }

    public static boolean matches(TargetConfig config) {
        return TYPE.equalsIgnoreCase(config.getType());
    }
}
