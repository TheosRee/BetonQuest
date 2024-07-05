package org.betonquest.betonquest.compatibility.npcs.abstractnpc;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.conditions.distance.NPCDistanceConditionFactory;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.conditions.location.NPCLocationConditionFactory;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.conditions.region.NPCRegionConditionFactory;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.events.teleport.NPCTeleportEventFactory;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.objectives.NPCInteractObjective;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.objectives.NPCRangeObjective;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.variables.npc.NPCVariableFactory;
import org.betonquest.betonquest.quest.registry.QuestTypeRegistries;
import org.betonquest.betonquest.quest.registry.type.ConditionTypeRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A standard {@link Integrator} for {@link BQNPCAdapter} which registers standard functionality.
 *
 * @param <T> the original npc type
 */
public abstract class NPCIntegrator<T> implements Integrator {
    /**
     * BetonQuest plugin instance.
     */
    protected final BetonQuest plugin;

    /**
     * Starts conversations by clicking on the npc.
     */
    @Nullable
    private NPCConversationStarter<T> conversationStarter;

    /**
     * The default Constructor.
     */
    public NPCIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    /**
     * Initialize the standard NPC features.
     *
     * @param prefix            the prefix used before any registered name for distinguishing
     * @param supplier          the supplier for new {@link BQNPCAdapter} instances
     * @param starter           the function to create a new {@link NPCConversationStarter} with
     *                          a given {@link BetonQuestLoggerFactory}
     * @param interactObjective the {@link NPCInteractObjective} class with a constructor of just
     *                          an {@link org.betonquest.betonquest.Instruction Instruction}
     * @param rangeObjective    the {@link NPCRangeObjective} class with a constructor of just
     *                          an {@link org.betonquest.betonquest.Instruction Instruction}
     */
    protected void hook(final String prefix, final Supplier<NPCSupplierStandard> supplier,
                        final Function<BetonQuestLoggerFactory, ? extends NPCConversationStarter<T>> starter,
                        final Class<? extends NPCInteractObjective> interactObjective, final Class<? extends NPCRangeObjective> rangeObjective) {
        final BetonQuestLoggerFactory loggerFactory = plugin.getLoggerFactory();

        conversationStarter = starter.apply(loggerFactory);

        final QuestTypeRegistries questRegistries = plugin.getQuestRegistries();

        // TODO NPC Hider

        plugin.registerObjectives(prefix + "interact", interactObjective);
        plugin.registerObjectives(prefix + "range", rangeObjective);

        questRegistries.getEventTypes().register(prefix + "teleport", new NPCTeleportEventFactory(supplier));

        // TODO ConversationIO?

        questRegistries.getVariableTypes().register(prefix, new NPCVariableFactory(loggerFactory, supplier));

        final ConditionTypeRegistry conditionTypes = questRegistries.getConditionTypes();
        conditionTypes.register(prefix + "distance", new NPCDistanceConditionFactory(supplier));
        conditionTypes.register(prefix + "location", new NPCLocationConditionFactory(supplier));

        if (Compatibility.getHooked().contains("WorldGuard")) {
            conditionTypes.register(prefix + "region", new NPCRegionConditionFactory(supplier));
        }
    }

    @Override
    public void reload() {
        if (conversationStarter != null) {
            conversationStarter.reload();
        }
    }

    @Override
    public void close() {
        // Empty
    }
}
