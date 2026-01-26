package dev.valhal.hytale.plugin.notifications.targets.factory;

import dev.valhal.hytale.plugin.notifications.targets.DiscordTarget;
import dev.valhal.hytale.plugin.notifications.targets.SlackTarget;
import dev.valhal.hytale.plugin.notifications.targets.WebhookTarget;
import dev.valhal.hytale.plugin.notifications.targets.NtfyTarget;

import java.util.Optional;

/**
 * Registers built-in target factories.
 */
public final class BuiltInTargetFactories {

    private BuiltInTargetFactories() {}

    /**
     * Registers all built-in target factories with the registry.
     * Should be called once at plugin startup.
     */
    public static void registerAll() {
        // ntfy target
        TargetRegistry.register(NtfyTarget.TYPE, (config, httpClient) ->
            Optional.of(new NtfyTarget(config, httpClient))
        );

        // Discord target
        TargetRegistry.register(DiscordTarget.TYPE, (config, httpClient) ->
            Optional.of(new DiscordTarget(config, httpClient))
        );

        // Slack target
        TargetRegistry.register(SlackTarget.TYPE, (config, httpClient) ->
            Optional.of(new SlackTarget(config, httpClient))
        );

        // Webhook target
        TargetRegistry.register(WebhookTarget.TYPE, (config, httpClient) ->
            Optional.of(new WebhookTarget(config, httpClient))
        );
    }
}
