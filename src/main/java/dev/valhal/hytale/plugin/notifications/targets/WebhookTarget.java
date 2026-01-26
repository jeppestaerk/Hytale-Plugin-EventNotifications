package dev.valhal.hytale.plugin.notifications.targets;

import dev.valhal.hytale.plugin.config.EventConfig;
import dev.valhal.hytale.plugin.config.TargetConfig;
import dev.valhal.hytale.plugin.notifications.NotificationEvent;
import dev.valhal.hytale.plugin.util.AsyncHttpClient;
import dev.valhal.hytale.plugin.util.JsonUtils;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Notification target for HTTP webhooks.
 */
public class WebhookTarget extends AbstractNotificationTarget {
    public static final String TYPE = "webhook";

    public WebhookTarget(TargetConfig config, AsyncHttpClient httpClient) {
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

        // Content type
        headers.put("Content-Type", config.getContentType());

        // Custom headers from config
        Map<String, String> customHeaders = config.getHeaders();
        if (customHeaders != null) {
            for (Map.Entry<String, String> entry : customHeaders.entrySet()) {
                // Don't override auth header if already set
                if (!entry.getKey().equalsIgnoreCase("Authorization") || !headers.containsKey("Authorization")) {
                    headers.put(entry.getKey(), entry.getValue());
                }
            }
        }

        // Build body
        String body = buildBody(event, eventConfig, data);

        return httpClient.post(getUrl(), body, headers);
    }

    private String buildBody(NotificationEvent event, EventConfig eventConfig, Map<String, String> data) {
        // Check for event-specific body template first
        String eventBody = eventConfig.getBodyTemplate();
        if (eventBody != null && !eventBody.isEmpty()) {
            return applyPlaceholders(eventBody, event, eventConfig, data);
        }

        // Fall back to target-level body template
        String targetBody = config.getBodyTemplate();
        if (targetBody != null && !targetBody.isEmpty()) {
            return applyPlaceholders(targetBody, event, eventConfig, data);
        }

        // Default JSON body
        return buildDefaultJsonBody(event, eventConfig, data);
    }

    private String applyPlaceholders(String template, NotificationEvent event,
                                      EventConfig eventConfig, Map<String, String> data) {
        String result = template;

        // Apply raw data placeholders
        for (Map.Entry<String, String> entry : data.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", JsonUtils.nullSafe(entry.getValue()));
            result = result.replace("{" + entry.getKey() + ":json}", JsonUtils.escape(entry.getValue()));
        }

        // Apply computed fields
        result = result.replace("{eventType}", event.eventType().getConfigKey());
        result = result.replace("{title}", eventConfig.formatTitle(data));
        result = result.replace("{message}", eventConfig.formatMessage(data));
        result = result.replace("{priority}", JsonUtils.nullSafe(eventConfig.getPriority()));
        result = result.replace("{tags}", JsonUtils.nullSafe(eventConfig.getTags()));

        // JSON-escaped versions
        result = result.replace("{title:json}", JsonUtils.escape(eventConfig.formatTitle(data)));
        result = result.replace("{message:json}", JsonUtils.escape(eventConfig.formatMessage(data)));

        return result;
    }

    private String buildDefaultJsonBody(NotificationEvent event, EventConfig eventConfig,
                                         Map<String, String> data) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"eventType\":").append(JsonUtils.quote(event.eventType().getConfigKey())).append(",");
        json.append("\"title\":").append(JsonUtils.quote(eventConfig.formatTitle(data))).append(",");
        json.append("\"message\":").append(JsonUtils.quote(eventConfig.formatMessage(data))).append(",");
        json.append("\"priority\":").append(JsonUtils.quote(eventConfig.getPriority())).append(",");
        json.append("\"tags\":").append(JsonUtils.quote(eventConfig.getTags())).append(",");
        json.append("\"data\":{");

        boolean first = true;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (!first) json.append(",");
            json.append(JsonUtils.quote(entry.getKey())).append(":").append(JsonUtils.quote(entry.getValue()));
            first = false;
        }

        json.append("}}");
        return json.toString();
    }
}
