package dev.valhal.hytale.plugin.notifications.targets;

import dev.valhal.hytale.plugin.config.EventConfig;
import dev.valhal.hytale.plugin.config.EventType;
import dev.valhal.hytale.plugin.config.TargetConfig;
import dev.valhal.hytale.plugin.notifications.NotificationEvent;
import dev.valhal.hytale.plugin.util.AsyncHttpClient;
import dev.valhal.hytale.plugin.util.JsonUtils;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Notification target for Discord webhooks.
 */
public class DiscordTarget extends AbstractNotificationTarget {
    public static final String TYPE = "discord";

    public DiscordTarget(TargetConfig config, AsyncHttpClient httpClient) {
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

        String body = buildDiscordPayload(event, eventConfig, data);

        return httpClient.post(getUrl(), body, headers);
    }

    private String buildDiscordPayload(NotificationEvent event, EventConfig eventConfig,
                                        Map<String, String> data) {
        StringBuilder json = new StringBuilder();
        json.append("{");

        // Bot username - use config value or fall back to server name
        String username = config.getDiscordUsername();
        if (username == null || username.isEmpty()) {
            username = data.getOrDefault("server", "");
        }
        if (!username.isEmpty()) {
            json.append("\"username\":").append(JsonUtils.quote(username)).append(",");
        }

        // Bot avatar
        String avatarUrl = config.getDiscordAvatarUrl();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            json.append("\"avatar_url\":").append(JsonUtils.quote(avatarUrl)).append(",");
        }

        // Check if we should use embed or plain content
        if (config.isDiscordUseEmbeds()) {
            json.append("\"embeds\":[");
            json.append(buildEmbed(event, eventConfig, data));
            json.append("]");
        } else {
            // Plain content message
            String title = eventConfig.formatTitle(data);
            String message = eventConfig.formatMessage(data);
            String content = title.isEmpty() ? message : "**" + title + "**\n" + message;
            json.append("\"content\":").append(JsonUtils.quote(content));
        }

        json.append("}");
        return json.toString();
    }

    private String buildEmbed(NotificationEvent event, EventConfig eventConfig,
                               Map<String, String> data) {
        StringBuilder embed = new StringBuilder();
        embed.append("{");

        // Title
        String title = eventConfig.formatTitle(data);
        if (title != null && !title.isEmpty()) {
            embed.append("\"title\":").append(JsonUtils.quote(title)).append(",");
        }

        // Description (message)
        String description = eventConfig.formatMessage(data);
        if (description != null && !description.isEmpty()) {
            embed.append("\"description\":").append(JsonUtils.quote(description)).append(",");
        }

        // Color - parse from event config or use default
        int color = parseColor(eventConfig.getColor(), getDefaultColor(event.eventType()));
        embed.append("\"color\":").append(color).append(",");

        // Timestamp
        embed.append("\"timestamp\":").append(JsonUtils.quote(Instant.now().toString()));

        // Footer with server name
        String server = data.getOrDefault("server", "");
        if (!server.isEmpty()) {
            embed.append(",\"footer\":{\"text\":").append(JsonUtils.quote(server)).append("}");
        }

        embed.append("}");
        return embed.toString();
    }

    private int parseColor(String colorStr, int defaultColor) {
        if (colorStr == null || colorStr.isEmpty()) {
            return defaultColor;
        }
        try {
            // Support hex colors like "#5865F2" or "5865F2"
            String hex = colorStr.startsWith("#") ? colorStr.substring(1) : colorStr;
            return Integer.parseInt(hex, 16);
        } catch (NumberFormatException e) {
            return defaultColor;
        }
    }

    private int getDefaultColor(EventType eventType) {
        return switch (eventType) {
            case SERVER_START -> 0x57F287;  // Green
            case SERVER_STOP -> 0xED4245;   // Red
            case PLAYER_JOIN -> 0x5865F2;   // Blurple
            case PLAYER_LEAVE -> 0x99AAB5;  // Gray
            case PLAYER_CHAT -> 0xFEE75C;   // Yellow
            case GROUP_PERMISSION_CHANGE, PLAYER_PERMISSION_CHANGE -> 0xE67E22; // Orange
            case GAME_MODE_CHANGE -> 0x9B59B6; // Purple
        };
    }
}
