package org.betonquest.betonquest.api.quest.npc.feature_citizens;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Teleport a Npc to a given location.
 */
public class NpcTeleportEvent implements NullableEvent {
    /**
     * The location to teleport the NPC to.
     */
    private final VariableLocation location;

    /**
     * The NPC wrapper.
     */
    private final NpcWrapper<?> npc;

    /**
     * Create a new Npc teleport event.
     *
     * @param npc      the npc wrapper of the Npc
     * @param location the location the Npc will be teleported to
     */
    public NpcTeleportEvent(final NpcWrapper<?> npc, final VariableLocation location) {
        this.npc = npc;
        this.location = location;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestRuntimeException {
        npc.getNpc().teleport(location.getValue(profile));
    }
}
