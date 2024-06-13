package org.betonquest.betonquest.instruction;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;

/**
 * Objectified parser for the Instruction.
 *
 * @param <T> what the argument returns
 */
public interface VariableArgument<T> {
    /**
     * {@link VariableLocation} argument.
     */
    VariableArgument<VariableLocation> LOCATION = VariableLocation::new;

    /**
     * {@link VariableNumber} argument.
     */
    VariableArgument<VariableNumber> NUMBER = VariableNumber::new;

    /**
     * Gets a {@link T} from string.
     *
     * @param variableProcessor the variable processor for resolving
     * @param pack              the source package
     * @param string            the string to parse
     * @return the {@link T}
     * @throws InstructionParseException when the string cannot be parsed as {@link T}
     */
    T convert(VariableProcessor variableProcessor, QuestPackage pack, String string) throws InstructionParseException;
}
