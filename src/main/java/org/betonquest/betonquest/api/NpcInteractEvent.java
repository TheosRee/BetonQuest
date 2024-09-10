package org.betonquest.betonquest.api;

import org.betonquest.betonquest.api.bukkit.event.ProfileEvent;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.bukkit.event.HandlerList;

/**
 * Event for interaction with BetonQuest {@link Npc}s.
 */
public class NpcInteractEvent extends ProfileEvent {
    /**
     * Static HandlerList to register listeners on tih event.
     */
    private static final HandlerList HANDLERS = new HandlerList();

    private final Action action;

    private final Npc<?> npc;

    public NpcInteractEvent(final Profile profile, final Npc<?> npc, final Action action) {
        super(profile);
        this.action = action;
        this.npc = npc;
    }

    /**
     * The static getter as required by the Event specification.
     *
     * @return the handler list to register new listener
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Gets the interacted Npc.
     *
     * @return the npc
     */
    public Npc<?> getNpc() {
        return npc;
    }

    public Action getAction() {
        return action;
    }

    public enum Action {
        LEFT,
        RIGHT
    }
}
