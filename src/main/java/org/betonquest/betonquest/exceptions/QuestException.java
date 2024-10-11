package org.betonquest.betonquest.exceptions;

import org.jetbrains.annotations.Nullable;

import java.io.Serial;

/**
 * Exception thrown when there is an error while using BetonQuest data types.
 *
 * @see InstructionParseException
 * @see QuestRuntimeException
 */
public class QuestException extends Exception {
    @Serial
    private static final long serialVersionUID = 3915478775320902973L;

    /**
     * {@link Exception#Exception(String)}
     *
     * @param message the displayed message.
     */
    public QuestException(final String message) {
        super(message);
    }

    /**
     * {@link Exception#Exception(String, Throwable)}
     *
     * @param message the exception message.
     * @param cause   the Throwable that caused this exception.
     */
    public QuestException(@Nullable final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * {@link Exception#Exception(Throwable)}
     *
     * @param cause the Throwable that caused this exception.
     */
    public QuestException(final Throwable cause) {
        super(cause);
    }
}
