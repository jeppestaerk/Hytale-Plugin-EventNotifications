package dev.valhal.hytale.plugin.notifications;

import dev.valhal.hytale.plugin.config.EventType;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for notification service targets.
 */
public interface NotificationService {
    /**
     * Gets the name of this notification target.
     */
    String getName();

    /**
     * Checks if this target is enabled.
     */
    boolean isEnabled();

    /**
     * Checks if a specific event type is enabled for this target.
     */
    boolean isEventEnabled(EventType eventType);

    /**
     * Sends a notification event asynchronously.
     *
     * @param event the event to send
     * @return a future that completes when the notification is sent
     */
    CompletableFuture<Void> send(NotificationEvent event);
}
