package org.betonquest.betonquest.api.quest.npc.feature_citizens;

import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.quest.npc.NpcFactory;
import org.betonquest.betonquest.api.quest.npc.conversation.NpcConversationStarter;
import org.betonquest.betonquest.compatibility.citizens.event.move.CitizensMoveController;
import org.betonquest.betonquest.quest.registry.processor.NpcProcessor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Starts new conversations with Citizen NPCs.
 */
public class CitizensConversationStarter extends NpcConversationStarter<NPC> {
    /**
     * Move Controller to check if the NPC blocks conversations while moving.
     */
    private final CitizensMoveController citizensMoveController;

    /**
     * Initializes the conversation starter for Citizens.
     *
     * @param plugin                 the plugin to register listener and load config
     * @param npcFactory             the factory to identify the clicked Npc
     * @param npcProcessor           the processor to start conversations on Npc interaction
     * @param citizensMoveController the move controller to check if the NPC currently blocks conversations
     */
    public CitizensConversationStarter(final BetonQuest plugin, final NpcFactory<NPC> npcFactory, final NpcProcessor npcProcessor,
                                       final CitizensMoveController citizensMoveController) {
        super(plugin, npcFactory, npcProcessor);
        this.citizensMoveController = citizensMoveController;
    }

    @Override
    protected Listener newLeftClickListener() {
        return new LeftClickListener();
    }

    @Override
    protected Listener newRightClickListener() {
        return new RightClickListener();
    }

    private void interactLogic(final NPCClickEvent event) {
        final NPC npc = event.getNPC();
        if (!citizensMoveController.blocksTalking(npc) && super.interactLogic(event.getClicker(), new CitizensBQAdapter(npc))) {
            event.setCancelled(true);
        }
    }

    /**
     * A listener for right-clicking a Citizens NPC.
     */
    private class RightClickListener implements Listener {
        /**
         * Create a new RightClickListener for Citizens NPCs.
         */
        public RightClickListener() {
        }

        /**
         * Handles right clicks.
         *
         * @param event the event to handle
         */
        @EventHandler(ignoreCancelled = true)
        public void onNPCClick(final NPCRightClickEvent event) {
            interactLogic(event);
        }
    }

    /**
     * A listener for left-clicking a Citizens NPC.
     */
    private class LeftClickListener implements Listener {
        /**
         * Create a new RightClickListener for Citizens NPCs.
         */
        public LeftClickListener() {
        }

        /**
         * Handles left click.
         *
         * @param event the event to handle
         */
        @EventHandler(ignoreCancelled = true)
        public void onNPCClick(final NPCLeftClickEvent event) {
            interactLogic(event);
        }
    }
}
