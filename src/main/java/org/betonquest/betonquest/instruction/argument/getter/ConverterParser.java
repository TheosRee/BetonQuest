package org.betonquest.betonquest.instruction.argument.getter;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public interface ConverterParser extends Parser {

    default <T> T[] getArray(final Converter<T> converter) throws InstructionParseException {
        return getArray(next(), converter);
    }

    @SuppressWarnings("unchecked")
    default <T> T[] getArray(@Nullable final String string, final Converter<T> converter) throws InstructionParseException {
        if (string == null) {
            return (T[]) new Object[0];
        }
        final String[] array = getArray(string);
        if (array.length == 0) {
            return (T[]) new Object[0];
        }

        final T first = converter.convert(array[0]);
        final T[] result = (T[]) Array.newInstance(first.getClass(), array.length);
        result[0] = first;

        for (int i = 1; i < array.length; i++) {
            result[i] = converter.convert(array[i]);
        }
        return result;
    }

    default <T> List<T> getList(final Converter<T> converter) throws InstructionParseException {
        return getList(next(), converter);
    }

    default <T> List<T> getList(@Nullable final String string, final Converter<T> converter) throws InstructionParseException {
        if (string == null) {
            return new ArrayList<>(0);
        }
        final String[] array = getArray(string);
        final List<T> list = new ArrayList<>(array.length);
        for (final String part : array) {
            list.add(converter.convert(part));
        }
        return list;
    }

    interface Converter<T> {
        T convert(String string) throws InstructionParseException;
    }
}
