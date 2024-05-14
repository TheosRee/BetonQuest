package org.betonquest.betonquest.compatibility.npcs.citizens;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.npcs.citizens.conditions.CitizensDistanceConditionFactory;
import org.betonquest.betonquest.compatibility.npcs.citizens.conditions.CitizensLocationConditionFactory;
import org.betonquest.betonquest.compatibility.npcs.citizens.conditions.CitizensRegionConditionFactory;
import org.betonquest.betonquest.compatibility.npcs.citizens.events.CitizensNPCTeleportEventFactory;
import org.betonquest.betonquest.compatibility.npcs.citizens.events.move.CitizensMoveController;
import org.betonquest.betonquest.compatibility.npcs.citizens.events.move.CitizensMoveEvent;
import org.betonquest.betonquest.compatibility.npcs.citizens.events.move.CitizensMoveEventFactory;
import org.betonquest.betonquest.compatibility.npcs.citizens.events.move.CitizensStopEventFactory;
import org.betonquest.betonquest.compatibility.npcs.citizens.objectives.CitizensInteractObjective;
import org.betonquest.betonquest.compatibility.npcs.citizens.objectives.CitizensRangeObjective;
import org.betonquest.betonquest.compatibility.npcs.citizens.variables.CitizensVariableFactory;
import org.betonquest.betonquest.compatibility.protocollib.hider.NPCHider;
import org.betonquest.betonquest.compatibility.protocollib.hider.UpdateVisibilityNowEvent;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.registry.QuestTypeRegistries;
import org.betonquest.betonquest.quest.registry.type.ConditionTypeRegistry;
import org.betonquest.betonquest.quest.registry.type.EventTypeRegistry;
import org.bukkit.Server;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitScheduler;

@SuppressWarnings({"PMD.CommentRequired", "NullAway.Init"})
public class CitizensIntegrator implements Integrator {
    /**
     * The active integrator instance.
     */
    private static CitizensIntegrator instance;

    private final BetonQuest plugin;

    private CitizensConversationStarter citizensConversationStarter;

    /**
     * Handles NPC movement of the {@link CitizensMoveEvent}.
     */
    private CitizensMoveController citizensMoveController;

    @SuppressWarnings("PMD.AssignmentToNonFinalStatic")
    public CitizensIntegrator() {
        instance = this;
        plugin = BetonQuest.getInstance();
    }

    /**
     * Gets the move controller used to start and stop NPC movement.
     *
     * @return the move controller of this NPC integration
     */
    public static CitizensMoveController getCitizensMoveInstance() {
        return instance.citizensMoveController;
    }

    @Override
    public void hook() {
        final BetonQuestLoggerFactory loggerFactory = BetonQuest.getInstance().getLoggerFactory();
        citizensMoveController = new CitizensMoveController(loggerFactory.create(CitizensMoveController.class));
        citizensConversationStarter = new CitizensConversationStarter(loggerFactory, loggerFactory.create(CitizensConversationStarter.class), citizensMoveController);
        new CitizensWalkingListener();

        final QuestTypeRegistries questRegistries = plugin.getQuestRegistries();
        final Server server = plugin.getServer();
        final BukkitScheduler scheduler = server.getScheduler();
        final PrimaryServerThreadData primaryServerThreadData = new PrimaryServerThreadData(server, scheduler, plugin);

        // if ProtocolLib is hooked, start NPCHider
        if (Compatibility.getHooked().contains("ProtocolLib")) {
            NPCHider.start(loggerFactory.create(NPCHider.class));
            plugin.registerEvents("updatevisibility", UpdateVisibilityNowEvent.class);
        }
        plugin.registerObjectives("npckill", NPCKillObjective.class);
        plugin.registerObjectives("npcinteract", CitizensInteractObjective.class);
        plugin.registerObjectives("npcrange", CitizensRangeObjective.class);
        server.getPluginManager().registerEvents(citizensMoveController, plugin);
        final EventTypeRegistry eventTypes = questRegistries.getEventTypes();
        eventTypes.register("movenpc", new CitizensMoveEventFactory(primaryServerThreadData, citizensMoveController));
        eventTypes.register("stopnpc", new CitizensStopEventFactory(primaryServerThreadData, citizensMoveController));
        eventTypes.register("teleportnpc", new CitizensNPCTeleportEventFactory(primaryServerThreadData));
        plugin.registerConversationIO("chest", CitizensInventoryConvIO.class);
        plugin.registerConversationIO("combined", CitizensInventoryConvIO.CitizensCombined.class);
        questRegistries.getVariableTypes().register("citizen", new CitizensVariableFactory(loggerFactory));
        final ConditionTypeRegistry conditionTypes = questRegistries.getConditionTypes();
        conditionTypes.register("npcdistance", new CitizensDistanceConditionFactory(primaryServerThreadData));
        conditionTypes.register("npclocation", new CitizensLocationConditionFactory(primaryServerThreadData));
        if (Compatibility.getHooked().contains("WorldGuard")) {
            conditionTypes.register("npcregion", new CitizensRegionConditionFactory(primaryServerThreadData));
        }
    }

    @Override
    public void reload() {
        citizensConversationStarter.reload();
    }

    @Override
    public void close() {
        HandlerList.unregisterAll(citizensMoveController);
    }
}
