package org.betonquest.betonquest.compatibility.npcs.fancynpcs;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.npcs.fancynpcs.conditions.FancyNpcsDistanceConditionFactory;
import org.betonquest.betonquest.compatibility.npcs.fancynpcs.conditions.FancyNpcsLocationConditionFactory;
import org.betonquest.betonquest.compatibility.npcs.fancynpcs.conditions.FancyNpcsRegionCondition;
import org.betonquest.betonquest.compatibility.npcs.fancynpcs.events.FancyNpcsNPCTeleportEventFactory;
import org.betonquest.betonquest.compatibility.npcs.fancynpcs.objectives.FancyNpcsInteractObjective;
import org.betonquest.betonquest.compatibility.npcs.fancynpcs.objectives.FancyNpcsRangeObjective;
import org.betonquest.betonquest.compatibility.npcs.fancynpcs.variables.FancyNpcVariableFactory;
import org.betonquest.betonquest.quest.registry.QuestTypeRegistries;
import org.betonquest.betonquest.quest.registry.type.ConditionTypeRegistry;

/**
 * Integrator implementation for the FancyNpcs plugin.
 */
public class FancyNpcsIntegrator implements Integrator {
    /**
     * The prefix used before any registered name for distinguishing.
     */
    private static final String PREFIX = "F-NPC-";

    /**
     * Starts a Conversation when interacting with the NPC.
     */
    private FancyNpcsConversationStarter conversationStarter;

    /**
     * The default Constructor.
     */
    public FancyNpcsIntegrator() {
    }

    @Override
    public void hook() {
        final BetonQuest plugin = BetonQuest.getInstance();
        final BetonQuestLoggerFactory loggerFactory = plugin.getLoggerFactory();
        conversationStarter = new FancyNpcsConversationStarter(loggerFactory, loggerFactory.create(FancyNpcsConversationStarter.class));
        /*
        -- new CitizensWalkingListener();
        -- plugin.registerEvents("movenpc", NPCMoveEvent.class);
        -- plugin.registerEvents("stopnpc", NPCStopEvent.class);
         */

        final QuestTypeRegistries questRegistries = plugin.getQuestRegistries();

        // if ProtocolLib is hooked, start NPCHider
        /*
        if (Compatibility.getHooked().contains("ProtocolLib")) {
            NPCHider.start(loggerFactory.create(NPCHider.class));
            plugin.registerEvents("updatevisibility", UpdateVisibilityNowEvent.class);
        }
         */
        //plugin.registerObjectives("npckill", NPCKillObjective.class);
        plugin.registerObjectives(PREFIX + "interact", FancyNpcsInteractObjective.class);
        plugin.registerObjectives(PREFIX + "range", FancyNpcsRangeObjective.class);
        questRegistries.getEventTypes().register(PREFIX + "teleport", new FancyNpcsNPCTeleportEventFactory());
        //plugin.registerConversationIO("chest", CitizensInventoryConvIO.class);
        //plugin.registerConversationIO("combined", CitizensInventoryConvIO.CitizensCombined.class);
        questRegistries.getVariableTypes().register(PREFIX, new FancyNpcVariableFactory(loggerFactory));
        final ConditionTypeRegistry conditionTypes = questRegistries.getConditionTypes();
        conditionTypes.register(PREFIX + "distance", new FancyNpcsDistanceConditionFactory());
        conditionTypes.register(PREFIX + "location", new FancyNpcsLocationConditionFactory());
        if (Compatibility.getHooked().contains("WorldGuard")) {
            conditionTypes.register(PREFIX + "region", new FancyNpcsRegionCondition());
        }
    }

    @Override
    public void reload() {
        conversationStarter.reload();
    }

    @Override
    public void close() {
        // Empty
    }
}
