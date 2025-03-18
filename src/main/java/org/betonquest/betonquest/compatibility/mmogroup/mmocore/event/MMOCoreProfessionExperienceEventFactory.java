package org.betonquest.betonquest.compatibility.mmogroup.mmocore.event;

import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.experience.Profession;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

/**
 * Factory to create {@link MMOCoreProfessionExperienceEvent}s from {@link Instruction}s.
 */
public class MMOCoreProfessionExperienceEventFactory implements EventFactory {

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new MMO Core Event Factory.
     *
     * @param data the data for primary server thread access
     */
    public MMOCoreProfessionExperienceEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final String professionName = instruction.next();
        final Profession profession;
        if (MMOCore.plugin.professionManager.has(professionName)) {
            profession = MMOCore.plugin.professionManager.get(professionName);
        } else {
            throw new QuestException("The profession could not be found!");
        }

        final VariableNumber amount = instruction.get(VariableNumber::new);
        final boolean isLevel = instruction.hasArgument("level");
        return new PrimaryServerThreadEvent(new MMOCoreProfessionExperienceEvent(profession, amount, isLevel), data);
    }
}
