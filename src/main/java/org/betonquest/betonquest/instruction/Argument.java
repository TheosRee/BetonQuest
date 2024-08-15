package org.betonquest.betonquest.instruction;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.BlockSelector;

/**
 * Objectified parser for the Instruction.
 *
 * @param <T> what the argument returns
 */
public interface Argument<T> {
    /**
     * {@link BlockSelector} argument.
     */
    Argument<BlockSelector> BLOCK_SELECTOR = BlockSelector::new;

    /**
     * Gets a {@link T} from string.
     *
     * @param string the string to parse
     * @return the {@link T}
     * @throws InstructionParseException when the string cannot be parsed as {@link T}
     */
    T convert(String string) throws InstructionParseException;
}
