package dev.valhal.hytale.plugin.notifications.targets;

import dev.valhal.hytale.plugin.config.EventConfig;
import dev.valhal.hytale.plugin.config.EventType;
import dev.valhal.hytale.plugin.config.TargetConfig;
import dev.valhal.hytale.plugin.notifications.NotificationEvent;
import dev.valhal.hytale.plugin.util.AsyncHttpClient;
import dev.valhal.hytale.plugin.util.JsonUtils;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Notification target for Slack webhooks.
 */
public class SlackTarget extends AbstractNotificationTarget {
    public static final String TYPE = "slack";

    public SlackTarget(TargetConfig config, AsyncHttpClient httpClient) {
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
        headers.put("Content-Type", "application/json");

        String body = buildSlackPayload(event, eventConfig, data);

        return httpClient.post(getUrl(), body, headers);
    }

    private String buildSlackPayload(NotificationEvent event, EventConfig eventConfig,
                                      Map<String, String> data) {
        StringBuilder json = new StringBuilder();
        json.append("{");

        // Bot username - use config value or fall back to server name
        String username = config.getSlackUsername();
        if (username == null || username.isEmpty()) {
            username = data.getOrDefault("server", "");
        }
        if (!username.isEmpty()) {
            json.append("\"username\":").append(JsonUtils.quote(username)).append(",");
        }

        // Icon - prefer emoji over URL
        String iconEmoji = config.getSlackIconEmoji();
        String iconUrl = config.getSlackIconUrl();
        if (iconEmoji != null && !iconEmoji.isEmpty()) {
            json.append("\"icon_emoji\":").append(JsonUtils.quote(iconEmoji)).append(",");
        } else if (iconUrl != null && !iconUrl.isEmpty()) {
            json.append("\"icon_url\":").append(JsonUtils.quote(iconUrl)).append(",");
        }

        // Check if we should use attachments or plain text
        if (config.isSlackUseAttachments()) {
            json.append("\"attachments\":[");
            json.append(buildAttachment(event, eventConfig, data));
            json.append("]");
        } else {
            // Plain text message
            String title = eventConfig.formatTitle(data);
            String message = eventConfig.formatMessage(data);
            String text = title.isEmpty() ? message : "*" + title + "*\n" + message;
            json.append("\"text\":").append(JsonUtils.quote(text));
        }

        json.append("}");
        return json.toString();
    }

    private String buildAttachment(NotificationEvent event, EventConfig eventConfig,
                                    Map<String, String> data) {
        StringBuilder attachment = new StringBuilder();
        attachment.append("{");

        // Fallback text (required for accessibility)
        String title = eventConfig.formatTitle(data);
        String message = eventConfig.formatMessage(data);
        String fallback = title.isEmpty() ? message : title + ": " + message;
        attachment.append("\"fallback\":").append(JsonUtils.quote(fallback)).append(",");

        // Color
        String color = eventConfig.getColor();
        if (color == null || color.isEmpty()) {
            color = getDefaultColor(event.eventType());
        }
        // Slack expects color without # prefix
        if (color.startsWith("#")) {
            color = color.substring(1);
        }
        attachment.append("\"color\":").append(JsonUtils.quote(color)).append(",");

        // Title
        if (title != null && !title.isEmpty()) {
            attachment.append("\"title\":").append(JsonUtils.quote(title)).append(",");
        }

        // Text (message body)
        if (message != null && !message.isEmpty()) {
            attachment.append("\"text\":").append(JsonUtils.quote(message)).append(",");
            attachment.append("\"mrkdwn_in\":[\"text\"],");
        }

        // Footer with server name
        String server = data.getOrDefault("server", "");
        if (!server.isEmpty()) {
            attachment.append("\"footer\":").append(JsonUtils.quote(server)).append(",");
        }

        // Timestamp
        attachment.append("\"ts\":").append(System.currentTimeMillis() / 1000);

        attachment.append("}");
        return attachment.toString();
    }

    private String getDefaultColor(EventType eventType) {
        return switch (eventType) {
            case SERVER_START -> "57F287";  // Green
            case SERVER_STOP -> "ED4245";   // Red
            case PLAYER_JOIN -> "5865F2";   // Blue
            case PLAYER_LEAVE -> "99AAB5";  // Gray
            case PLAYER_CHAT -> "FEE75C";   // Yellow
            case GROUP_PERMISSION_CHANGE, PLAYER_PERMISSION_CHANGE -> "E67E22"; // Orange
            case GAME_MODE_CHANGE -> "9B59B6"; // Purple
        };
    }
}
