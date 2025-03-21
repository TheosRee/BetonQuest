package org.betonquest.betonquest.quest.event.npc;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadStaticEvent;

/**
 * Factory for {@link NPCTeleportEvent} from the {@link Instruction}.
 */
public class NpcTeleportEventFactory implements EventFactory, StaticEventFactory {

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * Data to use for syncing to the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new factory for Npc Teleport Events.
     *
     * @param questTypeAPI the Quest Type API
     * @param data         the data to use for syncing to the primary server thread
     */
    public NpcTeleportEventFactory(final QuestTypeAPI questTypeAPI, final PrimaryServerThreadData data) {
        this.questTypeAPI = questTypeAPI;
        this.data = data;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadEvent(createNpcTeleportEvent(instruction), data);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadStaticEvent(createNpcTeleportEvent(instruction), data);
    }

    private NullableEventAdapter createNpcTeleportEvent(final Instruction instruction) throws QuestException {
        final NpcID npcId = instruction.getID(NpcID::new);
        final VariableLocation location = instruction.get(VariableLocation::new);
        final boolean spawn = instruction.hasArgument("spawn");
        return new NullableEventAdapter(new NPCTeleportEvent(questTypeAPI, npcId, location, spawn));
    }
}
