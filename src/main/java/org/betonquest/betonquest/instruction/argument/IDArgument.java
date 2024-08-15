package org.betonquest.betonquest.instruction.argument;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ID;

/**
 * Objectified parser for the Instruction.
 *
 * @param <T> what the argument returns
 */
public interface IDArgument<T extends ID> {
    /**
     * Gets a {@link T} from string.
     *
     * @param pack   the source package
     * @param string the string to parse
     * @return the {@link T}
     * @throws ObjectNotFoundException when the string cannot be parsed as {@link T}
     */
    T convert(QuestPackage pack, String string) throws ObjectNotFoundException;
}
