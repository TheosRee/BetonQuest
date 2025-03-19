package org.betonquest.betonquest.compatibility.quests;

import me.pikamug.quests.Quests;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

/**
 * Factory to create {@link QuestsEvent}s from {@link Instruction}s.
 */
public class QuestsEventFactory implements EventFactory {

    /**
     * Used Quests instance.
     */
    private final Quests quests;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the door event factory.
     *
     * @param quests active quests instance
     * @param data   the data for primary server thread access
     */
    public QuestsEventFactory(final Quests quests, final PrimaryServerThreadData data) {
        this.quests = quests;
        this.data = data;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final VariableString name = instruction.get(VariableString::new);
        final boolean override = instruction.hasArgument("check-requirements");
        return new PrimaryServerThreadEvent(new QuestsEvent(quests, name, override), data);
    }
}
