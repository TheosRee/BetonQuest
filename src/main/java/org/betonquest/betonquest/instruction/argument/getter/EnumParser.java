package org.betonquest.betonquest.instruction.argument.getter;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public interface EnumParser extends Parser {

    default <T extends Enum<T>> T getEnum(final Class<T> clazz) throws InstructionParseException {
        return getEnum(next(), clazz);
    }

    @Contract("null, _ -> null; !null, _ -> !null")
    @Nullable
    default <T extends Enum<T>> T getEnum(@Nullable final String string, final Class<T> clazz) throws InstructionParseException {
        return getEnum(string, clazz, null);
    }

    @Contract("_, _, !null -> !null")
    @Nullable
    <T extends Enum<T>> T getEnum(@Nullable String string, Class<T> clazz, @Nullable T defaultValue) throws InstructionParseException;
}
