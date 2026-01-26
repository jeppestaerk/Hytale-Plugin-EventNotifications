package dev.valhal.hytale.plugin.notifications.targets.factory;

import dev.valhal.hytale.plugin.config.TargetConfig;
import dev.valhal.hytale.plugin.notifications.NotificationService;
import dev.valhal.hytale.plugin.util.AsyncHttpClient;

import java.util.Optional;

/**
 * Factory interface for creating notification targets.
 * Implementations create targets of a specific type.
 */
public interface TargetFactory {
    /**
     * Gets the type identifier this factory handles.
     *
     * @return the type identifier (e.g., "ntfy", "discord", "webhook")
     */
    String getType();

    /**
     * Creates a notification target from the given configuration.
     *
     * @param config the target configuration
     * @param httpClient the HTTP client to use for requests
     * @return the created target, or empty if creation fails
     */
    Optional<NotificationService> create(TargetConfig config, AsyncHttpClient httpClient);

    /**
     * Creates a factory that always returns the same type.
     */
    static TargetFactory of(String type, TargetCreator creator) {
        return new TargetFactory() {
            @Override
            public String getType() {
                return type;
            }

            @Override
            public Optional<NotificationService> create(TargetConfig config, AsyncHttpClient httpClient) {
                return creator.create(config, httpClient);
            }
        };
    }

    /**
     * Functional interface for target creation.
     */
    @FunctionalInterface
    interface TargetCreator {
        Optional<NotificationService> create(TargetConfig config, AsyncHttpClient httpClient);
    }
}
