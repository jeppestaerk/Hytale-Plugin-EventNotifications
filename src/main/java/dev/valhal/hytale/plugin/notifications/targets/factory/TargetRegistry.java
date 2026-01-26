package dev.valhal.hytale.plugin.notifications.targets.factory;

import dev.valhal.hytale.plugin.config.TargetConfig;
import dev.valhal.hytale.plugin.notifications.NotificationService;
import dev.valhal.hytale.plugin.util.AsyncHttpClient;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for notification target factories.
 * Allows dynamic registration of target types.
 */
public class TargetRegistry {
    private static final Map<String, TargetFactory> factories = new ConcurrentHashMap<>();

    private TargetRegistry() {}

    /**
     * Registers a target factory.
     *
     * @param factory the factory to register
     */
    public static void register(TargetFactory factory) {
        factories.put(factory.getType().toLowerCase(), factory);
    }

    /**
     * Registers a target factory with explicit type.
     *
     * @param type the type identifier
     * @param creator the creator function
     */
    public static void register(String type, TargetFactory.TargetCreator creator) {
        register(TargetFactory.of(type, creator));
    }

    /**
     * Unregisters a target factory.
     *
     * @param type the type to unregister
     */
    public static void unregister(String type) {
        factories.remove(type.toLowerCase());
    }

    /**
     * Creates a target using the registered factory for the given type.
     *
     * @param type the target type
     * @param config the target configuration
     * @param httpClient the HTTP client
     * @return the created target, or empty if no factory is registered or creation fails
     */
    public static Optional<NotificationService> createTarget(String type, TargetConfig config, AsyncHttpClient httpClient) {
        TargetFactory factory = factories.get(type.toLowerCase());
        if (factory == null) {
            return Optional.empty();
        }
        return factory.create(config, httpClient);
    }

    /**
     * Creates a target using the type from the configuration.
     *
     * @param config the target configuration (must have type field set)
     * @param httpClient the HTTP client
     * @return the created target, or empty if type is unknown or creation fails
     */
    public static Optional<NotificationService> createTarget(TargetConfig config, AsyncHttpClient httpClient) {
        String type = config.getType();
        if (type == null || type.isEmpty()) {
            return Optional.empty();
        }
        return createTarget(type, config, httpClient);
    }

    /**
     * Checks if a factory is registered for the given type.
     */
    public static boolean isRegistered(String type) {
        return factories.containsKey(type.toLowerCase());
    }

    /**
     * Clears all registered factories.
     */
    public static void clear() {
        factories.clear();
    }
}
