package dev.valhal.hytale.plugin.notifications.targets;

import dev.valhal.hytale.plugin.config.EventConfig;
import dev.valhal.hytale.plugin.config.TargetConfig;
import dev.valhal.hytale.plugin.notifications.NotificationEvent;
import dev.valhal.hytale.plugin.util.AsyncHttpClient;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Notification target for ntfy.sh push notifications.
 */
public class NtfyTarget extends AbstractNotificationTarget {
    public static final String TYPE = "ntfy";

    public NtfyTarget(TargetConfig config, AsyncHttpClient httpClient) {
        super(config, httpClient);
    }

    @Override
    public String getName() {
        return TYPE;
    }

    @Override
    protected CompletableFuture<Void> doSend(NotificationEvent event, EventConfig eventConfig) {
        Map<String, String> data = event.data();
        Map<String, String> headers = buildBaseHeaders();

        // Content type - markdown if enabled
        if (config.isMarkdown()) {
            headers.put("Content-Type", "text/markdown");
        } else {
            headers.put("Content-Type", "text/plain");
        }

        // Title
        String title = eventConfig.formatTitle(data);
        if (title != null && !title.isEmpty()) {
            headers.put("X-Title", title);
        }

        // Priority - event config overrides target default
        String priority = eventConfig.getPriority();
        if (priority == null || priority.isEmpty()) {
            priority = config.getDefaultPriority();
        }
        if (priority != null && !priority.isEmpty() && !priority.equals("default")) {
            headers.put("X-Priority", priority);
        }

        // Tags
        String tags = eventConfig.getTags();
        if (tags != null && !tags.isEmpty()) {
            headers.put("X-Tags", tags);
        }

        // Icon - event config overrides target default
        String icon = eventConfig.getIcon();
        if (icon == null || icon.isEmpty()) {
            icon = config.getDefaultIcon();
        }
        if (icon != null && !icon.isEmpty()) {
            headers.put("X-Icon", icon);
        }

        // Message body
        String message = eventConfig.formatMessage(data);

        return httpClient.post(getUrl(), message, headers);
    }
}
