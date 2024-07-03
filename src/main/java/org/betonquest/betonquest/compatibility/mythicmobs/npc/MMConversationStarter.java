package org.betonquest.betonquest.compatibility.mythicmobs.npc;

import io.lumine.mythic.bukkit.events.MythicMobInteractEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCConversationStarter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Starts new conversations with MythicMobs ActiveMobs.
 */
public class MMConversationStarter extends NPCConversationStarter<ActiveMob> {
    /**
     * Initializes the listener.
     *
     * @param loggerFactory the logger factory used to create logger for the started conversations
     * @param log           the custom logger instance for this class
     */
    public MMConversationStarter(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger log) {
        super(loggerFactory, log);
    }

    @Override
    protected Listener newLeftClickListener() {
        return new Listener() {
            // There is no "left click"
        };
    }

    @Override
    protected Listener newRightClickListener() {
        return new Listener() {
            /**
             * Starts the conversation on right click.
             */
            @EventHandler(ignoreCancelled = true)
            public void onRight(final MythicMobInteractEvent event) {
                if (interactLogic(event.getPlayer(), () -> new MMBQAdapter(event.getActiveMob()))) {
                    event.setCancelled();
                }
            }
        };
    }
}
