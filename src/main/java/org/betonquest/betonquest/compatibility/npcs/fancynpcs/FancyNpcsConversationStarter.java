package org.betonquest.betonquest.compatibility.npcs.fancynpcs;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCConversationStarter;
import org.betonquest.betonquest.id.ConversationID;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Starts new conversations with FancyNpcs NPCs.
 */
public class FancyNpcsConversationStarter extends NPCConversationStarter {
    /**
     * Initializes the listener.
     *
     * @param loggerFactory the logger factory used to create logger for the started conversations
     * @param log           the custom logger instance for this class
     */
    public FancyNpcsConversationStarter(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger log) {
        super(loggerFactory, log);
    }

    @Override
    protected Listener newLeftClickListener() {
        return new Listener() {
            /**
             * Starts the conversation on left click.
             */
            @EventHandler(ignoreCancelled = true)
            public void onLeft(final NpcInteractEvent event) {
                if (event.getInteractionType() == NpcInteractEvent.InteractionType.LEFT_CLICK && interactLogic(event)) {
                    event.setCancelled(true);
                }
            }
        };
    }

    @Override
    protected Listener newRightClickListener() {
        return new Listener() {
            /**
             * Starts the conversation on right click.
             */
            @EventHandler(ignoreCancelled = true)
            public void onRight(final NpcInteractEvent event) {
                if (event.getInteractionType() == NpcInteractEvent.InteractionType.RIGHT_CLICK && interactLogic(event)) {
                    event.setCancelled(true);
                }
            }
        };
    }

    private boolean interactLogic(final NpcInteractEvent event) {
        final Npc npc = event.getNpc();
        return super.interactLogic(event.getPlayer(), () -> new FanyNpcsBQAdapter(npc));
    }

    @Override
    protected void startConversation(final OnlineProfile onlineProfile, final ConversationID conversationID, final BQNPCAdapter npc) {
        if (!(npc instanceof FanyNpcsBQAdapter)) {
            throw new IllegalArgumentException("The NPC Adapter is not a FancyNpcs Adapter!");
        }
        new FancyNpcsConversation(loggerFactory.create(FancyNpcsConversation.class), onlineProfile, conversationID,
                npc.getLocation(), ((FanyNpcsBQAdapter) npc).getFancyNpcsNPC(), npc);
    }
}
