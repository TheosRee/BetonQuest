package org.betonquest.betonquest.compatibility.npc.simplenpcs;

import com.github.arnhav.api.NPCClickEvent;
import com.github.arnhav.api.NPCLeftClickEvent;
import com.github.arnhav.api.NPCRightClickEvent;
import com.github.arnhav.objects.SNPC;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.npc.feature.NpcInteractCatcher;
import org.betonquest.betonquest.kernel.registry.quest.NpcTypeRegistry;
import org.betonquest.betonquest.quest.objective.interact.Interaction;
import org.bukkit.event.EventHandler;

/**
 * Catches interaction with SimpleNPCs Npcs.
 */
public class SimpleNPCsCatcher extends NpcInteractCatcher<SNPC> {
    /**
     * Initializes the SimpleNPCs catcher.
     *
     * @param profileProvider the profile provider instance
     * @param npcTypeRegistry the registry to identify the clicked Npc
     */
    public SimpleNPCsCatcher(final ProfileProvider profileProvider, final NpcTypeRegistry npcTypeRegistry) {
        super(profileProvider, npcTypeRegistry);
    }

    /**
     * Handles right clicks.
     *
     * @param event the event to handle
     */
    @EventHandler(ignoreCancelled = true)
    public void onNPCClick(final NPCRightClickEvent event) {
        interactLogic(event, Interaction.RIGHT);
    }

    /**
     * Handles left click.
     *
     * @param event the event to handle
     */
    @EventHandler(ignoreCancelled = true)
    public void onNPCClick(final NPCLeftClickEvent event) {
        interactLogic(event, Interaction.LEFT);
    }

    private void interactLogic(final NPCClickEvent event, final Interaction interaction) {
        final SNPC npc = event.getNPC();
        if (super.interactLogic(event.getClicker(), new SimpleNPCsAdapter(npc), interaction,
                false, event.isAsynchronous())) {
            event.setCancelled(true);
        }
    }
}
