package org.betonquest.betonquest.instruction.argument;

import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Objectified parser for the Instruction.
 *
 * @param <T> what the argument returns
 */
public interface Argument<T> {
    /**
     * Gets a {@link T} from string.
     *
     * @param string the string to parse
     * @return the {@link T}
     * @throws InstructionParseException when the string cannot be parsed as {@link T}
     */
    T convert(String string) throws InstructionParseException;
}
