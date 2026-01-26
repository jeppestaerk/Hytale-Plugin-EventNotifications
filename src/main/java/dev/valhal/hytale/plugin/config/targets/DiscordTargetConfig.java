package dev.valhal.hytale.plugin.config.targets;

import dev.valhal.hytale.plugin.config.TargetConfig;

/**
 * Configuration specific to Discord webhook targets.
 * Provides typed accessors for Discord-specific options.
 */
public class DiscordTargetConfig {
    public static final String TYPE = "discord";

    private final TargetConfig config;

    public DiscordTargetConfig(TargetConfig config) {
        this.config = config;
    }

    public TargetConfig getBaseConfig() {
        return config;
    }

    public String getUsername() {
        return config.getDiscordUsername();
    }

    public String getAvatarUrl() {
        return config.getDiscordAvatarUrl();
    }

    public boolean isUseEmbeds() {
        return config.isDiscordUseEmbeds();
    }

    public static boolean matches(TargetConfig config) {
        return TYPE.equalsIgnoreCase(config.getType());
    }
}
