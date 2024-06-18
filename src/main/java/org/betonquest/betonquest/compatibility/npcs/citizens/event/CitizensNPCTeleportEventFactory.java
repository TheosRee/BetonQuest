package org.betonquest.betonquest.compatibility.npcs.citizens.event;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.ComposedEvent;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.event.teleport.NPCTeleportEventFactory;
import org.betonquest.betonquest.compatibility.npcs.citizens.CitizensNPCSupplier;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadComposedEvent;

/**
 * Citizens implementation for {@link NPCTeleportEventFactory}.
 */
public class CitizensNPCTeleportEventFactory extends NPCTeleportEventFactory {
    /**
     * Data to use for syncing to the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new factory for Citizens teleport events.
     *
     * @param data the data to use for syncing to the primary server thread
     */
    public CitizensNPCTeleportEventFactory(final PrimaryServerThreadData data) {
        super(() -> CitizensNPCSupplier::getSupplierByIDStatic);
        this.data = data;
    }

    @Override
    public ComposedEvent parseComposedEvent(final Instruction instruction) throws InstructionParseException {
        return new PrimaryServerThreadComposedEvent(super.parseComposedEvent(instruction), data);
    }
}
