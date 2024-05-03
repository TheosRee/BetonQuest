package org.betonquest.betonquest.compatibility.npcs.abstractnpc.conditions.location;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.quest.condition.Condition;
import org.betonquest.betonquest.api.quest.condition.ConditionFactory;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCSupplierStandard;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.location.CompoundLocation;

import java.util.function.Supplier;

/**
 * Factory to create {@link NPCLocationCondition}s from {@link Instruction}s.
 */
public abstract class NPCLocationConditionFactory implements ConditionFactory, NPCSupplierStandard {
    /**
     * The default Constructor.
     */
    public NPCLocationConditionFactory() {
    }

    @Override
    public Condition parse(final Instruction instruction) throws InstructionParseException {
        final String npcId = instruction.next();
        final Supplier<BQNPCAdapter> supplier = getSupplierByID(npcId);
        final CompoundLocation location = instruction.getLocation();
        final VariableNumber radius = instruction.getVarNum();
        return new NPCLocationCondition(npcId, supplier, location, radius);
    }
}
