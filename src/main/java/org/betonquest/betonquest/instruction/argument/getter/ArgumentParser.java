package org.betonquest.betonquest.instruction.argument.getter;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public interface ArgumentParser extends Parser {

    default <T> T fun(final Argument<T> argument) throws InstructionParseException {
        return fun(next(), argument);
    }

    @Contract("!null, _ -> !null")
    @Nullable
    default <T> T fun(@Nullable final String string, final Argument<T> argument) throws InstructionParseException {
        if (string == null) {
            return null;
        }
        return argument.convert(string);
    }

    default <T> T fun(final VariableArgument<T> argument) throws InstructionParseException {
        return fun(next(), argument);
    }

    @Contract("!null, _ -> !null")
    @Nullable
    <T> T fun(@Nullable String string, VariableArgument<T> argument) throws InstructionParseException;
}
