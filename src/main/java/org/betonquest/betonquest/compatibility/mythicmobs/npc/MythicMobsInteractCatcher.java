package org.betonquest.betonquest.compatibility.mythicmobs.npc;

import io.lumine.mythic.bukkit.events.MythicMobInteractEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.npc.feature.NpcInteractCatcher;
import org.betonquest.betonquest.kernel.registry.quest.NpcTypeRegistry;
import org.betonquest.betonquest.objective.EntityInteractObjective.Interaction;
import org.bukkit.event.EventHandler;

/**
 * Catches interactions with MythicMobs and redirect them to BetonQuest Npc-Events.
 */
public class MythicMobsInteractCatcher extends NpcInteractCatcher<ActiveMob> {
    /**
     * Initializes the interact catcher.
     *
     * @param profileProvider the profile provider instance
     * @param npcTypeRegistry the registry to identify the clicked Npc
     */
    public MythicMobsInteractCatcher(final ProfileProvider profileProvider, final NpcTypeRegistry npcTypeRegistry) {
        super(profileProvider, npcTypeRegistry);
    }

    // TODO? "left click" aka hit

    /**
     * Catches a right click.
     *
     * @param event the interact event
     */
    @EventHandler
    public void onRight(final MythicMobInteractEvent event) {
        if (interactLogic(event.getPlayer(), new MythicMobsNpcAdapter(event.getActiveMob()), Interaction.RIGHT,
                event.isCancelled(), event.isAsynchronous())) {
            event.setCancelled();
        }
    }
}
