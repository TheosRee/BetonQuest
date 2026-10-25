package org.betonquest.betonquest.compatibility.mythicmobs;

import io.lumine.mythiccrucible.events.MythicCraftItemEvent;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.ItemStackCraftedEvent;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.quest.objective.crafting.CraftingObjective;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 * Integrator for MythicCrucible (MythicMobs Items).
 */
public class MythicCrucibleIntegrator implements Integration {

    /**
     * The minimum required version of MythicCrucible.
     */
    public static final String REQUIRED_VERSION = "5.13.1-SNAPSHOT";

    /**
     * Creates a new MythicCrucibleIntegrator.
     */
    public MythicCrucibleIntegrator() {
    }

    @Override
    public void enable(final BetonQuestApi api) throws QuestException {
        final BetonQuestLogger logger = api.loggerFactory().create(getClass());
        final ProfileProvider profileProvider = api.profiles();
        api.bukkit().registerEvents(new ItemsCraftObjectiveAdder(profileProvider, logger));
    }

    @Override
    public void postEnable(final BetonQuestApi betonQuestApi) throws QuestException {
        // Empty
    }

    @Override
    public void disable() throws QuestException {
        // Empty
    }

    /**
     * Allows a {@link CraftingObjective} to also process crafting items using Mythic Crafting.
     *
     * @param profileProvider Profile Provider to get profiles from Players.
     * @param logger          Custom logger for debugging
     */
    public record ItemsCraftObjectiveAdder(ProfileProvider profileProvider,
                                           BetonQuestLogger logger) implements Listener {

//        @EventHandler(priority = EventPriority.MONITOR)
//        public void test(final MythicCraftItemEvent event) {
//            logger.info(event.toString());
//            logger.info(event.getRecipeKey().toString());
//            logger.info(event.getResults().toString());
//        }

        /**
         * This listener handles items that were crafted by Crucible's recipes.
         *
         * @param event the event to process
         */
        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onCraft(final MythicCraftItemEvent event) {
            final OnlineProfile profile = profileProvider.getProfile((Player) event.getTrigger().getWhoClicked());
            for (final ItemStack result : event.getResults()) {
                new ItemStackCraftedEvent(profile, result, result.getAmount()).callEvent();
            }
        }
    }
}
