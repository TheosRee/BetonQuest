package org.betonquest.betonquest.compatibility.npcs.citizens.condition;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.Condition;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.condition.location.NPCLocationCondition;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.condition.location.NPCLocationConditionFactory;
import org.betonquest.betonquest.compatibility.npcs.citizens.CitizensNPCSupplier;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadCondition;

/**
 * Citizens implementation of {@link NPCLocationCondition}.
 */
public class CitizensLocationConditionFactory extends NPCLocationConditionFactory implements CitizensNPCSupplier {
    /**
     * Data used for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new factory for Citizens NPC location conditions.
     *
     * @param data the data used for primary server thread access
     */
    public CitizensLocationConditionFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public Condition parse(final Instruction instruction) throws InstructionParseException {
        return new PrimaryServerThreadCondition(super.parse(instruction), data);
    }
}
