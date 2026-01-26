package dev.valhal.hytale.plugin.config.targets;

import dev.valhal.hytale.plugin.config.TargetConfig;

/**
 * Configuration specific to Slack notification targets.
 * Provides typed accessors for Slack-specific options.
 */
public class SlackTargetConfig {
    public static final String TYPE = "slack";

    private final TargetConfig config;

    public SlackTargetConfig(TargetConfig config) {
        this.config = config;
    }

    public TargetConfig getBaseConfig() {
        return config;
    }

    public String getSlackUsername() {
        return config.getSlackUsername();
    }

    public String getSlackIconUrl() {
        return config.getSlackIconUrl();
    }

    public String getSlackIconEmoji() {
        return config.getSlackIconEmoji();
    }

    public boolean isSlackUseAttachments() {
        return config.isSlackUseAttachments();
    }

    public static boolean matches(TargetConfig config) {
        return TYPE.equalsIgnoreCase(config.getType());
    }
}
