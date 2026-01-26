package dev.valhal.hytale.plugin.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.valhal.hytale.plugin.config.validation.ConfigValidationResult;
import dev.valhal.hytale.plugin.config.validation.ConfigValidator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Main plugin configuration with validation support.
 */
public class PluginConfig {
    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(EventType.class, new EventType.GsonAdapter())
        .create();

    private boolean enabled = true;
    private Map<String, TargetConfig> targets;

    public boolean isEnabled() {
        return enabled;
    }

    public Map<String, TargetConfig> getTargets() {
        return targets;
    }

    public TargetConfig getTarget(String name) {
        return targets != null ? targets.get(name) : null;
    }

    /**
     * Loads configuration from path with validation.
     */
    public static PluginConfig load(Path configPath) throws IOException {
        if (Files.exists(configPath)) {
            String content = Files.readString(configPath, StandardCharsets.UTF_8);
            PluginConfig config = GSON.fromJson(content, PluginConfig.class);

            // Validate
            ConfigValidator validator = new ConfigValidator();
            ConfigValidationResult result = validator.validate(config);
            if (result.hasWarnings()) {
                System.err.println("Configuration warnings:\n" + result);
            }

            return config;
        }

        PluginConfig defaultConfig = loadDefaults();
        save(configPath, defaultConfig);
        return defaultConfig;
    }

    public static PluginConfig loadDefaults() throws IOException {
        try (InputStream is = PluginConfig.class.getResourceAsStream("/default-config.json")) {
            if (is == null) {
                throw new IOException("Default config not found in resources");
            }
            try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                return GSON.fromJson(reader, PluginConfig.class);
            }
        }
    }

    public static void save(Path configPath, PluginConfig config) throws IOException {
        Files.createDirectories(configPath.getParent());
        String json = GSON.toJson(config);
        Files.writeString(configPath, json, StandardCharsets.UTF_8);
    }
}
