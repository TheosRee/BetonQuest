package org.betonquest.betonquest.quest.event.run;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.utils.PlayerConverter;

import java.util.List;

/**
 * Create new {@link RunForAllEvent} from instruction.
 */
public class RunForAllEventFactory implements StaticEventFactory {

    /**
     * Create new {@link RunForAllEventFactory}.
     */
    public RunForAllEventFactory() {
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws InstructionParseException {
        final List<EventID> events = instruction.getList(instruction.getOptional("events"), string -> instruction.getID(EventID::new));
        final List<ConditionID> conditions = instruction.getList(instruction.getOptional("where"), string -> instruction.getID(ConditionID::new));
        return new RunForAllEvent(PlayerConverter::getOnlineProfiles, events, conditions);
    }
}
