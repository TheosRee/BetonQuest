package org.betonquest.betonquest.api.compatibility;

import org.jetbrains.annotations.Nullable;

import java.io.Serial;

@SuppressWarnings("PMD.CommentRequired")
public interface IntegratorFactory {
    Integrator create() throws IntegrationException;

    class IntegrationException extends Exception {
        @Serial
        private static final long serialVersionUID = -838121807177805269L;

        public IntegrationException(@Nullable final String message) {
            super(message);
        }

        public IntegrationException(@Nullable final String message, final Throwable cause) {
            super(message, cause);
        }
    }
}
