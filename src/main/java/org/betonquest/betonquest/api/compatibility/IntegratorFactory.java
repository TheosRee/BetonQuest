package org.betonquest.betonquest.api.compatibility;

import org.jetbrains.annotations.Nullable;

import java.io.Serial;

/**
 * Factory to create a new Compatibility/Integration.
 */
@FunctionalInterface
public interface IntegratorFactory {
    /**
     * Creates a new integration.
     *
     * @return the newly created integration
     * @throws IntegrationException when the integration could not be created
     */
    Integrator create() throws IntegrationException;

    /**
     * Exception to throw when an integration creation fails.
     */
    class IntegrationException extends Exception {
        @Serial
        private static final long serialVersionUID = -838121807177805269L;

        /**
         * Creates a new Exception.
         *
         * @param message the exception message
         */
        public IntegrationException(@Nullable final String message) {
            super(message);
        }

        /**
         * Creates a new Exception.
         *
         * @param message the exception message
         * @param cause   the exception cause
         */
        public IntegrationException(@Nullable final String message, final Throwable cause) {
            super(message, cause);
        }
    }
}
