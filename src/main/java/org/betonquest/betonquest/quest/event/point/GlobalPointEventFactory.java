package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableIdentifier;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

import java.util.Locale;

/**
 * Factory to create global points events from {@link Instruction}s.
 */
public class GlobalPointEventFactory implements EventFactory, StaticEventFactory {
    /**
     * The global data.
     */
    private final GlobalData globalData;

    /**
     * Create the global points event factory.
     *
     * @param globalData the global data
     */
    public GlobalPointEventFactory(final GlobalData globalData) {
        this.globalData = globalData;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        return parseCombinedEvent(instruction);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws QuestException {
        return parseCombinedEvent(instruction);
    }

    private NullableEventAdapter parseCombinedEvent(final Instruction instruction) throws QuestException {
        return new NullableEventAdapter(createGlobalPointEvent(instruction));
    }

    private GlobalPointEvent createGlobalPointEvent(final Instruction instruction) throws QuestException {
        final VariableIdentifier category = instruction.get(VariableIdentifier::new);
        final String number = instruction.next();
        final String action = instruction.getOptional("action");
        if (action != null) {
            try {
                final Point type = Point.valueOf(action.toUpperCase(Locale.ROOT));
                return new GlobalPointEvent(globalData, category, instruction.get(number, VariableNumber::new), type);
            } catch (final IllegalArgumentException e) {
                throw new QuestException("Unknown modification action: " + instruction.current(), e);
            }
        }
        if (!number.isEmpty() && number.charAt(0) == '*') {
            return new GlobalPointEvent(globalData, category, instruction.get(number.replace("*", ""), VariableNumber::new), Point.MULTIPLY);
        }
        return new GlobalPointEvent(globalData, category, instruction.get(number, VariableNumber::new), Point.ADD);
    }
}
