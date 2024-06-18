package org.betonquest.betonquest.compatibility.npcs.fancynpcs;

import de.oliver.fancynpcs.api.Npc;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.bukkit.Location;

/**
 * FancyNpcs Compatibility Adapter for general BetonQuest NPC behaviour.
 */
public class FanyNpcsBQAdapter implements BQNPCAdapter {
    /**
     * The FancyNpcs NPC instance.
     */
    private final Npc npc;

    /**
     * Create a new FancyNpcs NPC Adapter.
     *
     * @param npc the FancyNpcs NPC instance
     */
    public FanyNpcsBQAdapter(final Npc npc) {
        this.npc = npc;
    }

    /**
     * Gets the real FancyNpcs NPC.
     *
     * @return the adapted Citizens NPC
     */
    public final Npc getFancyNpcsNPC() {
        return npc;
    }

    @Override
    public String getId() {
        return npc.getData().getId();
    }

    @Override
    public String getName() {
        return npc.getData().getDisplayName();
    }

    @Override
    public String getFormattedName() {
        // TODO is this the full name?
        return npc.getData().getName();
    }

    @Override
    public Location getLocation() {
        return npc.getData().getLocation();
    }

    @Override
    public void teleport(final Location location) {
        npc.getData().setLocation(location);
        npc.moveForAll();
    }
}
