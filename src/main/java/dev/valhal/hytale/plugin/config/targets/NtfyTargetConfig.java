package dev.valhal.hytale.plugin.config.targets;

import dev.valhal.hytale.plugin.config.TargetConfig;

/**
 * Configuration specific to ntfy notification targets.
 * Provides typed accessors for ntfy-specific options.
 */
public class NtfyTargetConfig {
    public static final String TYPE = "ntfy";

    private final TargetConfig config;

    public NtfyTargetConfig(TargetConfig config) {
        this.config = config;
    }

    public TargetConfig getBaseConfig() {
        return config;
    }

    public boolean isMarkdown() {
        return config.isMarkdown();
    }

    public String getDefaultPriority() {
        return config.getDefaultPriority();
    }

    public String getDefaultIcon() {
        return config.getDefaultIcon();
    }

    public static boolean matches(TargetConfig config) {
        return TYPE.equalsIgnoreCase(config.getType());
    }
}
