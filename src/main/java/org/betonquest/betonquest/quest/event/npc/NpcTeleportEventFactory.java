package org.betonquest.betonquest.quest.event.npc;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.registry.processor.NpcProcessor;

/**
 * Factory for {@link NPCTeleportEvent} from the {@link Instruction}.
 */
public class NpcTeleportEventFactory implements EventFactory, StaticEventFactory {
    /**
     * Processor to get npc.
     */
    private final NpcProcessor npcProcessor;

    /**
     * Create a new factory for Npc Teleport Events.
     *
     * @param npcProcessor the processor to get npc
     */
    public NpcTeleportEventFactory(final NpcProcessor npcProcessor) {
        this.npcProcessor = npcProcessor;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        return createNpcTeleportEvent(instruction);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws InstructionParseException {
        return createNpcTeleportEvent(instruction);
    }

    private NullableEventAdapter createNpcTeleportEvent(final Instruction instruction) throws InstructionParseException {
        final NpcID npcId = instruction.getNpc();
        final VariableLocation location = instruction.getLocation();
        return new NullableEventAdapter(new NPCTeleportEvent(npcProcessor, npcId, location));
    }
}
