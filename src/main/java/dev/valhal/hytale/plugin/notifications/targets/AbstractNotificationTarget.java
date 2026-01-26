package dev.valhal.hytale.plugin.notifications.targets;

import dev.valhal.hytale.plugin.config.EventConfig;
import dev.valhal.hytale.plugin.config.EventType;
import dev.valhal.hytale.plugin.config.TargetConfig;
import dev.valhal.hytale.plugin.notifications.NotificationEvent;
import dev.valhal.hytale.plugin.notifications.NotificationService;
import dev.valhal.hytale.plugin.util.AsyncHttpClient;
import dev.valhal.hytale.plugin.util.AuthUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Abstract base class for notification targets.
 * Uses template method pattern - send() handles common logic, subclasses implement doSend().
 */
public abstract class AbstractNotificationTarget implements NotificationService {
    protected final TargetConfig config;
    protected final AsyncHttpClient httpClient;
    private final String authHeader;

    protected AbstractNotificationTarget(TargetConfig config, AsyncHttpClient httpClient) {
        this.config = config;
        this.httpClient = httpClient;
        this.authHeader = buildAuthHeader();
    }

    private String buildAuthHeader() {
        if (config.hasBearerAuth()) {
            return AuthUtils.buildBearerAuth(config.getBearerToken());
        }
        if (config.hasBasicAuth()) {
            return AuthUtils.buildBasicAuth(config.getUsername(), config.getPassword());
        }
        return null;
    }

    @Override
    public boolean isEnabled() {
        return config != null && config.isEnabled();
    }

    @Override
    public boolean isEventEnabled(EventType eventType) {
        return config.isEventEnabled(eventType);
    }

    /**
     * Template method that handles common send logic.
     * Subclasses should implement doSend() for target-specific behavior.
     */
    @Override
    public final CompletableFuture<Void> send(NotificationEvent event) {
        if (!isEnabled() || !isEventEnabled(event.eventType())) {
            return CompletableFuture.completedFuture(null);
        }

        EventConfig eventConfig = config.getEvent(event.eventType());
        if (eventConfig == null) {
            return CompletableFuture.completedFuture(null);
        }

        return doSend(event, eventConfig);
    }

    /**
     * Subclasses implement this to perform target-specific sending.
     *
     * @param event the notification event
     * @param eventConfig the event configuration
     * @return a future that completes when the notification is sent
     */
    protected abstract CompletableFuture<Void> doSend(NotificationEvent event, EventConfig eventConfig);

    /**
     * Builds base headers with auth if configured.
     */
    protected Map<String, String> buildBaseHeaders() {
        Map<String, String> headers = new HashMap<>();
        if (authHeader != null) {
            headers.put("Authorization", authHeader);
        }
        return headers;
    }

    /**
     * Gets the configured URL for this target.
     */
    protected String getUrl() {
        return config.getUrl();
    }

    /**
     * Gets the target configuration.
     */
    protected TargetConfig getConfig() {
        return config;
    }
}
