package org.betonquest.betonquest.compatibility.npcs.citizens.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.conditions.distance.NPCDistanceCondition;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.conditions.distance.NPCDistanceConditionFactory;
import org.betonquest.betonquest.compatibility.npcs.citizens.CitizensNPCSupplier;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

/**
 * Citizens implementation of {@link NPCDistanceCondition}.
 */
public class CitizensDistanceConditionFactory extends NPCDistanceConditionFactory implements CitizensNPCSupplier {
    /**
     * Data used for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new factory for Citizens NPC distance conditions.
     *
     * @param data the data used for primary server thread access
     */
    public CitizensDistanceConditionFactory(final PrimaryServerThreadData data) {
        super();
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws InstructionParseException {
        // TODO onlineplayer require
        return new PrimaryServerThreadPlayerCondition(super.parsePlayer(instruction), data);
    }
}
