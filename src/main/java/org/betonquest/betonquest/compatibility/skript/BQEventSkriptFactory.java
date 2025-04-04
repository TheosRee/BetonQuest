package org.betonquest.betonquest.compatibility.skript;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

/**
 * Factory to create {@link BQEventSkript}s from {@link Instruction}s.
 */
public class BQEventSkriptFactory implements PlayerEventFactory {
    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the door event factory.
     *
     * @param data the data for primary server thread access
     */
    public BQEventSkriptFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final VariableString identifier = instruction.get(VariableString::new);
        return new PrimaryServerThreadEvent(new BQEventSkript(identifier), data);
    }
}
