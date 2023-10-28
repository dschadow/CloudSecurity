package de.dominikschadow.configclient.transit;

/**
 * Transit engine payload, consisting of a key name and a payload to use (can be
 * used to store plaintext and ciphertext).
 *
 * @author Dominik Schadow
 */
public record TransitPayload(String keyName, String payload) {
}