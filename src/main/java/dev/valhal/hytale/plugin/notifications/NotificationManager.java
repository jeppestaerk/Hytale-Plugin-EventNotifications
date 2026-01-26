package dev.valhal.hytale.plugin.notifications;

import com.hypixel.hytale.logger.HytaleLogger;
import dev.valhal.hytale.plugin.config.EventType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Manages notification dispatch to all registered targets.
 */
public class NotificationManager {
    private final HytaleLogger logger;
    private final List<NotificationService> targets = new ArrayList<>();

    public NotificationManager(HytaleLogger logger) {
        this.logger = logger;
    }

    public void registerTarget(NotificationService target) {
        targets.add(target);
        logger.atInfo().log("Registered notification target: %s", target.getName());
    }

    public void send(NotificationEvent event) {
        for (NotificationService target : targets) {
            if (target.isEnabled()) {
                target.send(event)
                    .exceptionally(ex -> {
                        logger.atSevere().withCause(ex).log(
                            "Failed to send notification via %s for event %s",
                            target.getName(), event.eventType().getConfigKey()
                        );
                        return null;
                    });
            }
        }
    }

    public CompletableFuture<Void> sendAndWait(NotificationEvent event) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (NotificationService target : targets) {
            if (target.isEnabled()) {
                futures.add(target.send(event)
                    .exceptionally(ex -> {
                        logger.atSevere().withCause(ex).log(
                            "Failed to send notification via %s for event %s",
                            target.getName(), event.eventType().getConfigKey()
                        );
                        return null;
                    }));
            }
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    public void shutdown() {
        targets.clear();
    }
}
