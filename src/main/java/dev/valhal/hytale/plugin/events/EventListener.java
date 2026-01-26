package dev.valhal.hytale.plugin.events;

import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.event.events.BootEvent;
import com.hypixel.hytale.server.core.event.events.ShutdownEvent;
import com.hypixel.hytale.server.core.event.events.ecs.ChangeGameModeEvent;
import com.hypixel.hytale.server.core.event.events.permissions.GroupPermissionChangeEvent;
import com.hypixel.hytale.server.core.event.events.permissions.PlayerPermissionChangeEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import dev.valhal.hytale.plugin.config.EventType;
import dev.valhal.hytale.plugin.notifications.NotificationEvent;
import dev.valhal.hytale.plugin.notifications.NotificationManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Listens to Hytale server events and dispatches notifications.
 */
public class EventListener {
    private final NotificationManager notificationManager;
    private final EventRegistry eventRegistry;
    private final HytaleLogger logger;

    public EventListener(NotificationManager notificationManager, EventRegistry eventRegistry, HytaleLogger logger) {
        this.notificationManager = notificationManager;
        this.eventRegistry = eventRegistry;
        this.logger = logger;
    }

    public void register() {
        logger.atInfo().log("Registering event listeners");

        eventRegistry.register(BootEvent.class, this::onBoot);
        eventRegistry.register(ShutdownEvent.class, this::onShutdown);
        eventRegistry.register(PlayerConnectEvent.class, this::onPlayerConnect);
        eventRegistry.register(PlayerDisconnectEvent.class, this::onPlayerDisconnect);
        eventRegistry.registerAsyncGlobal(PlayerChatEvent.class, this::onPlayerChatAsync);

        // Permission events
        eventRegistry.register(GroupPermissionChangeEvent.Added.class, this::onGroupPermissionAdded);
        eventRegistry.register(GroupPermissionChangeEvent.Removed.class, this::onGroupPermissionRemoved);
        eventRegistry.register(PlayerPermissionChangeEvent.GroupAdded.class, this::onPlayerGroupAdded);
        eventRegistry.register(PlayerPermissionChangeEvent.GroupRemoved.class, this::onPlayerGroupRemoved);
        eventRegistry.register(PlayerPermissionChangeEvent.PermissionsAdded.class, this::onPlayerPermissionsAdded);
        eventRegistry.register(PlayerPermissionChangeEvent.PermissionsRemoved.class, this::onPlayerPermissionsRemoved);

        // Game mode change event
        eventRegistry.registerGlobal(ChangeGameModeEvent.class, this::onChangeGameMode);
    }

    private void onBoot(BootEvent event) {
        send(EventType.SERVER_START, Map.of());
    }

    private void onShutdown(ShutdownEvent event) {
        NotificationEvent notificationEvent = buildEvent(EventType.SERVER_STOP, Map.of());
        notificationManager.sendAndWait(notificationEvent).join();
    }

    private void onPlayerConnect(PlayerConnectEvent event) {
        String playerName = event.getPlayerRef().getUsername();
        send(EventType.PLAYER_JOIN, Map.of("player", playerName));
    }

    private void onPlayerDisconnect(PlayerDisconnectEvent event) {
        String playerName = event.getPlayerRef().getUsername();
        send(EventType.PLAYER_LEAVE, Map.of("player", playerName));
    }

    private CompletableFuture<PlayerChatEvent> onPlayerChatAsync(CompletableFuture<PlayerChatEvent> future) {
        return future.thenApply(event -> {
            String playerName = event.getSender().getUsername();
            String chatMessage = event.getContent();
            send(EventType.PLAYER_CHAT, Map.of(
                "player", playerName,
                "message", chatMessage
            ));
            return event;
        });
    }

    private void onGroupPermissionAdded(GroupPermissionChangeEvent.Added event) {
        String groupName = event.getGroupName();
        String permissions = String.join(", ", event.getAddedPermissions());
        send(EventType.GROUP_PERMISSION_CHANGE, Map.of(
            "group", groupName,
            "action", "added",
            "permissions", permissions
        ));
    }

    private void onGroupPermissionRemoved(GroupPermissionChangeEvent.Removed event) {
        String groupName = event.getGroupName();
        String permissions = String.join(", ", event.getRemovedPermissions());
        send(EventType.GROUP_PERMISSION_CHANGE, Map.of(
            "group", groupName,
            "action", "removed",
            "permissions", permissions
        ));
    }

    private void onPlayerGroupAdded(PlayerPermissionChangeEvent.GroupAdded event) {
        UUID playerUuid = event.getPlayerUuid();
        String groupName = event.getGroupName();
        send(EventType.PLAYER_PERMISSION_CHANGE, Map.of(
            "player", playerUuid.toString(),
            "action", "added to group",
            "group", groupName
        ));
    }

    private void onPlayerGroupRemoved(PlayerPermissionChangeEvent.GroupRemoved event) {
        UUID playerUuid = event.getPlayerUuid();
        String groupName = event.getGroupName();
        send(EventType.PLAYER_PERMISSION_CHANGE, Map.of(
            "player", playerUuid.toString(),
            "action", "removed from group",
            "group", groupName
        ));
    }

    private void onPlayerPermissionsAdded(PlayerPermissionChangeEvent.PermissionsAdded event) {
        UUID playerUuid = event.getPlayerUuid();
        String permissions = String.join(", ", event.getAddedPermissions());
        send(EventType.PLAYER_PERMISSION_CHANGE, Map.of(
            "player", playerUuid.toString(),
            "action", "granted permissions",
            "permissions", permissions
        ));
    }

    private void onPlayerPermissionsRemoved(PlayerPermissionChangeEvent.PermissionsRemoved event) {
        UUID playerUuid = event.getPlayerUuid();
        String permissions = String.join(", ", event.getRemovedPermissions());
        send(EventType.PLAYER_PERMISSION_CHANGE, Map.of(
            "player", playerUuid.toString(),
            "action", "revoked permissions",
            "permissions", permissions
        ));
    }

    private void onChangeGameMode(ChangeGameModeEvent event) {
        String gameMode = event.getGameMode().name();
        send(EventType.GAME_MODE_CHANGE, Map.of(
            "gamemode", gameMode
        ));
    }

    private void send(EventType eventType, Map<String, String> data) {
        NotificationEvent event = buildEvent(eventType, data);
        notificationManager.send(event);
    }

    private NotificationEvent buildEvent(EventType eventType, Map<String, String> data) {
        // Add server name to all events
        Map<String, String> enrichedData = new HashMap<>(data);
        String serverName = HytaleServer.get().getServerName();
        enrichedData.put("server", serverName != null ? serverName : "Hytale Server");
        return new NotificationEvent(eventType, enrichedData);
    }
}
