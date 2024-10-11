package org.betonquest.betonquest.instruction.variable;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.id.ID;
import org.betonquest.betonquest.instruction.argument.IDArgument;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;

/**
 * A variable of an {@link ID}.
 * <p>
 * Not to mistake with a {@link org.betonquest.betonquest.id.VariableID}!
 *
 * @param <T> the type of id
 */
public class VariableIDTheOther<T extends ID> extends Variable<T> {
    /**
     * Resolves a string that may contain variables to a variable of an {@link ID}.
     *
     * @param variableProcessor the processor to create the variables
     * @param pack              the package in which the variable is used in
     * @param input             the string that may contain variables
     * @param argument          the argument to convert the resolved variable to the id
     * @throws QuestException if the variables could not be created or resolved to the given type
     */
    public VariableIDTheOther(final VariableProcessor variableProcessor, final QuestPackage pack, final String input,
                              final IDArgument<T> argument) throws QuestException {
        super(variableProcessor, pack, input, value -> argument.convert(pack, value));
    }
}
