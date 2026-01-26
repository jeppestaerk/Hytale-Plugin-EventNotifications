package dev.valhal.hytale.plugin.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Utility methods for building authentication headers.
 */
public final class AuthUtils {

    private AuthUtils() {}

    /**
     * Builds an Authorization header value for Basic authentication.
     *
     * @param username the username
     * @param password the password
     * @return the Authorization header value (e.g., "Basic dXNlcjpwYXNz")
     */
    public static String buildBasicAuth(String username, String password) {
        String credentials = username + ":" + password;
        String encoded = Base64.getEncoder().encodeToString(
            credentials.getBytes(StandardCharsets.UTF_8)
        );
        return "Basic " + encoded;
    }

    /**
     * Builds an Authorization header value for Bearer token authentication.
     *
     * @param token the bearer token
     * @return the Authorization header value (e.g., "Bearer token123")
     */
    public static String buildBearerAuth(String token) {
        return "Bearer " + token;
    }

    /**
     * Checks if a string has a non-empty value.
     */
    public static boolean hasValue(String value) {
        return value != null && !value.isEmpty();
    }
}
