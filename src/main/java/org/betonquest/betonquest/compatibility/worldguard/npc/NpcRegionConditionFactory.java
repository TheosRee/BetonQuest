package org.betonquest.betonquest.compatibility.worldguard.npc;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerlessCondition;

/**
 * Factory to create {@link NpcRegionCondition}s from {@link Instruction}s.
 */
public class NpcRegionConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {
    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * Data used for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new factory for NPC Region Conditions.
     *
     * @param questTypeAPI the Quest Type API
     * @param data         the data for primary server thread access
     */
    public NpcRegionConditionFactory(final QuestTypeAPI questTypeAPI, final PrimaryServerThreadData data) {
        this.questTypeAPI = questTypeAPI;
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadPlayerCondition(parseInstruction(instruction), data);
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadPlayerlessCondition(parseInstruction(instruction), data);
    }

    private NullableConditionAdapter parseInstruction(final Instruction instruction) throws QuestException {
        final NpcID npcId = instruction.getID(NpcID::new);
        final VariableString region = instruction.get(VariableString::new);
        return new NullableConditionAdapter(new NpcRegionCondition(questTypeAPI, npcId, region));
    }
}
