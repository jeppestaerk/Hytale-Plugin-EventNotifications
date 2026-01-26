package dev.valhal.hytale.plugin;

import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.logger.HytaleLogger;
import dev.valhal.hytale.plugin.config.PluginConfig;
import dev.valhal.hytale.plugin.config.TargetConfig;
import dev.valhal.hytale.plugin.events.EventListener;
import dev.valhal.hytale.plugin.notifications.NotificationManager;
import dev.valhal.hytale.plugin.notifications.targets.factory.BuiltInTargetFactories;
import dev.valhal.hytale.plugin.notifications.targets.factory.TargetRegistry;
import dev.valhal.hytale.plugin.util.AsyncHttpClient;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Main plugin class for Hytale server notifications.
 */
public class EventNotificationsPlugin extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final String CONFIG_FILE = "config.json";

    private PluginConfig config;
    private NotificationManager notificationManager;
    private EventListener eventListener;
    private AsyncHttpClient httpClient;

    public EventNotificationsPlugin(JavaPluginInit init) {
        super(init);
        LOGGER.atInfo().log("Hello from " + this.getName() + " version " + this.getManifest().getVersion().toString());
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("Setting up plugin " + this.getName());

        try {
            loadConfig();
        } catch (IOException e) {
            LOGGER.atSevere().withCause(e).log("Failed to load configuration");
            return;
        }

        initializeServices();
        registerEventListeners();

        LOGGER.atInfo().log("Plugin " + this.getName() + " setup complete");
    }

    @Override
    protected void shutdown() {
        LOGGER.atInfo().log("Shutting down " + this.getName());

        if (notificationManager != null) {
            notificationManager.shutdown();
        }

        if (httpClient != null) {
            httpClient.shutdown();
        }
    }

    private void loadConfig() throws IOException {
        Path configPath = getDataDirectory().resolve(CONFIG_FILE);
        config = PluginConfig.load(configPath);
        LOGGER.atInfo().log("Configuration loaded from %s", configPath);
    }

    private void initializeServices() {
        httpClient = new AsyncHttpClient(Executors.newVirtualThreadPerTaskExecutor());
        notificationManager = new NotificationManager(LOGGER);

        // Register built-in target factories
        BuiltInTargetFactories.registerAll();

        // Create targets from configuration using the registry
        Map<String, TargetConfig> targets = config.getTargets();
        if (targets != null) {
            for (Map.Entry<String, TargetConfig> entry : targets.entrySet()) {
                String targetName = entry.getKey();
                TargetConfig targetConfig = entry.getValue();

                if (!targetConfig.isEnabled()) {
                    LOGGER.atInfo().log("Skipping disabled target: %s", targetName);
                    continue;
                }

                String type = targetConfig.getType();
                if (type.isEmpty()) {
                    LOGGER.atWarning().log("Target '%s' has no type specified, skipping", targetName);
                    continue;
                }

                TargetRegistry.createTarget(targetConfig, httpClient)
                    .ifPresentOrElse(
                        target -> {
                            notificationManager.registerTarget(target);
                            LOGGER.atInfo().log("Registered %s notification target: %s", type, targetName);
                        },
                        () -> LOGGER.atWarning().log("Unknown target type '%s' for target '%s'", type, targetName)
                    );
            }
        }
    }

    private void registerEventListeners() {
        if (!config.isEnabled()) {
            LOGGER.atInfo().log("Event notifications are disabled in configuration");
            return;
        }

        EventRegistry eventRegistry = getEventRegistry();
        eventListener = new EventListener(notificationManager, eventRegistry, LOGGER);
        eventListener.register();
    }

}
