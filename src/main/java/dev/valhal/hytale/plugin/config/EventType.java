package dev.valhal.hytale.plugin.config;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Optional;

/**
 * Type-safe enum for all supported notification event types.
 */
public enum EventType {
    SERVER_START("serverStart"),
    SERVER_STOP("serverStop"),
    PLAYER_JOIN("playerJoin"),
    PLAYER_LEAVE("playerLeave"),
    PLAYER_CHAT("playerChat"),
    GROUP_PERMISSION_CHANGE("groupPermissionChange"),
    PLAYER_PERMISSION_CHANGE("playerPermissionChange"),
    GAME_MODE_CHANGE("gameModeChange");

    private final String configKey;

    EventType(String configKey) {
        this.configKey = configKey;
    }

    /**
     * Gets the config key used in JSON configuration.
     */
    public String getConfigKey() {
        return configKey;
    }

    /**
     * Finds an EventType by its config key.
     *
     * @param key the config key (e.g., "serverStart")
     * @return the EventType, or empty if not found
     */
    public static Optional<EventType> fromConfigKey(String key) {
        if (key == null) {
            return Optional.empty();
        }
        for (EventType type : values()) {
            if (type.configKey.equals(key)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }

    /**
     * Checks if the given config key is a valid EventType.
     */
    public static boolean isValid(String key) {
        return fromConfigKey(key).isPresent();
    }

    @Override
    public String toString() {
        return configKey;
    }

    /**
     * GSON TypeAdapter for serializing EventType as its config key string.
     */
    public static class GsonAdapter extends TypeAdapter<EventType> {
        @Override
        public void write(JsonWriter out, EventType value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.getConfigKey());
            }
        }

        @Override
        public EventType read(JsonReader in) throws IOException {
            String key = in.nextString();
            return fromConfigKey(key).orElse(null);
        }
    }
}
