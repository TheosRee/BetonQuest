package org.betonquest.betonquest.instruction.argument.getter;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.ID;
import org.betonquest.betonquest.instruction.argument.IDArgument;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public interface IDParser extends Parser {

    default <T extends ID> T getID(final IDArgument<T> argument) throws InstructionParseException {
        return getID(next(), argument);
    }

    @Contract("!null, _ -> !null")
    @Nullable
    <T extends ID> T getID(@Nullable String string, IDArgument<T> argument) throws InstructionParseException;

    default <T extends ID> T[] getIDArray(final IDArgument<T> argument) throws InstructionParseException {
        return getIDArray(next(), argument);
    }

    <T extends ID> T[] getIDArray(@Nullable String string, IDArgument<T> argument) throws InstructionParseException;
}
