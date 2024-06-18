package org.betonquest.betonquest.compatibility.npcs.fancynpcs.objectives;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.objectives.NPCRangeObjective;
import org.betonquest.betonquest.compatibility.npcs.fancynpcs.FancyNpcsSupplierStandard;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * FancyNpcs implementation of {@link NPCRangeObjective}.
 */
public class FancyNpcsRangeObjective extends NPCRangeObjective implements FancyNpcsSupplierStandard {
    /**
     * Creates a new FancyNPC RangeObjective from the given instruction.
     *
     * @param instruction the user-provided instruction
     * @throws InstructionParseException if the instruction is invalid
     */
    public FancyNpcsRangeObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
    }
}
