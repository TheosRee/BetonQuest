package org.betonquest.betonquest.quest.event.npc;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

/**
 * Teleports a Npc to a given location.
 */
public class NPCTeleportEvent implements NullableEvent {

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * The npc id.
     */
    private final NpcID npcId;

    /**
     * The location to teleport the Npc to.
     */
    private final VariableLocation location;

    /**
     * Spawns the Npc if not already spawned.
     */
    private final boolean spawn;

    /**
     * Create a new Npc Teleport Event.
     *
     * @param questTypeAPI the Quest Type API
     * @param npcId        the npc id
     * @param location     the location the Npc will be teleported to
     * @param spawn        if the npc should be spawned if not in the world
     */
    public NPCTeleportEvent(final QuestTypeAPI questTypeAPI, final NpcID npcId, final VariableLocation location, final boolean spawn) {
        this.questTypeAPI = questTypeAPI;
        this.npcId = npcId;
        this.location = location;
        this.spawn = spawn;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final Location loc = location.getValue(profile);
        final Npc<?> npc = questTypeAPI.getNpc(npcId);
        if (npc.isSpawned()) {
            npc.teleport(loc);
        } else if (spawn) {
            npc.spawn(loc);
        }
    }
}
