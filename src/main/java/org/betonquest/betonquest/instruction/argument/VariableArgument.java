package org.betonquest.betonquest.instruction.argument;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.betonquest.betonquest.utils.Utils;

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
     * {@link VariableNumber} argument with {@link VariableNumber#NOT_LESS_THAN_ONE_CHECKER}.
     */
    VariableArgument<VariableNumber> NUMBER_NOT_LESS_THAN_ONE = (variableProcessor, pack, input) ->
            new VariableNumber(variableProcessor, pack, input, VariableNumber.NOT_LESS_THAN_ONE_CHECKER);

    /**
     * {@link VariableString} argument which adds the package as identifier, if not already present.
     */
    VariableArgument<VariableString> STRING_WITH_PACKAGE = (variableProcessor, pack, input) ->
            new VariableString(variableProcessor, pack, Utils.addPackage(pack, input));

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
