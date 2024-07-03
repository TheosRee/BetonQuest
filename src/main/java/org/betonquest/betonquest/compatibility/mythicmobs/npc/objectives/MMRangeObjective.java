package org.betonquest.betonquest.compatibility.mythicmobs.npc.objectives;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.compatibility.mythicmobs.MythicMobsIntegrator;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.objectives.NPCRangeObjective;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * MythicMobs implementation of {@link NPCRangeObjective}.
 */
public class MMRangeObjective extends NPCRangeObjective {
    /**
     * Creates a new MythicMobs RangeObjective from the given instruction.
     *
     * @param instruction the user-provided instruction
     * @throws InstructionParseException if the instruction is invalid
     */
    public MMRangeObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction, () -> MythicMobsIntegrator::getSupplierByIDStatic);
    }
}
