package dev.valhal.hytale.plugin.util;

import com.hypixel.hytale.logger.HytaleLogger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class AsyncHttpClient {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    private final HttpClient httpClient;

    public AsyncHttpClient(Executor executor) {
        this.httpClient = HttpClient.newBuilder()
            .executor(executor)
            .connectTimeout(DEFAULT_TIMEOUT)
            .build();
    }

    public CompletableFuture<Void> post(String url, String body, Map<String, String> headers) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(DEFAULT_TIMEOUT)
            .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));

        for (Map.Entry<String, String> header : headers.entrySet()) {
            requestBuilder.header(header.getKey(), header.getValue());
        }

        HttpRequest request = requestBuilder.build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(response -> {
                int statusCode = response.statusCode();
                if (statusCode >= 200 && statusCode < 300) {
                    LOGGER.atFine().log("HTTP POST to %s succeeded with status %d", url, statusCode);
                } else {
                    LOGGER.atWarning().log("HTTP POST to %s failed with status %d: %s",
                        url, statusCode, response.body());
                }
            });
    }

    public void shutdown() {
        httpClient.close();
    }
}
