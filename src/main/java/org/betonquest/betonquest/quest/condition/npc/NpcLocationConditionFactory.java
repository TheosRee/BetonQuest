package org.betonquest.betonquest.quest.condition.npc;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.registry.processor.NpcProcessor;

/**
 * Factory to create {@link NpcLocationCondition}s from {@link Instruction}s.
 */
public class NpcLocationConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {
    /**
     * Processor to get npc.
     */
    private final NpcProcessor npcProcessor;

    /**
     * Create a new factory for NPC Location Conditions.
     *
     * @param npcProcessor the processor to get npc
     */
    public NpcLocationConditionFactory(final NpcProcessor npcProcessor) {
        this.npcProcessor = npcProcessor;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws InstructionParseException {
        return parseNpcLocationCondition(instruction);
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws InstructionParseException {
        return parseNpcLocationCondition(instruction);
    }

    private NullableConditionAdapter parseNpcLocationCondition(final Instruction instruction) throws InstructionParseException {
        final NpcID npcId = instruction.getNpc();
        final VariableLocation location = instruction.getLocation();
        final VariableNumber radius = instruction.getVarNum();
        return new NullableConditionAdapter(new NpcLocationCondition(npcProcessor, npcId, location, radius));
    }
}
