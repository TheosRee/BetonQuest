package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.condition.MMOCoreAttributeConditionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.condition.MMOCoreClassConditionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.condition.MMOCoreProfessionLevelConditionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.event.MMOCoreAttributePointsEventFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.event.MMOCoreAttributeReallocationPointsEventFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.event.MMOCoreClassExperienceEventFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.event.MMOCoreClassPointsEventFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.event.MMOCoreProfessionExperienceEventFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.event.MMOCoreSkillPointsEventFactory;
import org.betonquest.betonquest.kernel.registry.quest.ConditionTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.EventTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.ObjectiveTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.QuestTypeRegistries;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Integrator for MMO CORE.
 */
public class MMOCoreIntegrator implements Integrator {
    /**
     * The logger for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Util class to get and validate attributes.
     */
    @Nullable
    private MMOCoreUtils mmoCoreUtils;

    /**
     * The default constructor.
     */
    public MMOCoreIntegrator() {
        log = BetonQuest.getInstance().getLoggerFactory().create(MMOCoreIntegrator.class);
    }

    @Override
    public void hook() {
        try {
            mmoCoreUtils = new MMOCoreUtils(BetonQuest.getInstance().getConfigAccessorFactory(), Bukkit.getPluginManager().getPlugin("MMOCore").getDataFolder());
        } catch (FileNotFoundException | InvalidConfigurationException e) {
            log.warn("Couldn't load the MMOCore attribute configuration file!", e);
            return;
        }

        final BetonQuest plugin = BetonQuest.getInstance();
        final Server server = plugin.getServer();
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);
        final QuestTypeRegistries questRegistries = plugin.getQuestRegistries();
        final ConditionTypeRegistry conditionTypes = questRegistries.condition();
        conditionTypes.register("mmoclass", new MMOCoreClassConditionFactory(data));
        conditionTypes.register("mmoattribute", new MMOCoreAttributeConditionFactory(data, mmoCoreUtils));
        conditionTypes.register("mmoprofession", new MMOCoreProfessionLevelConditionFactory(data));

        final ObjectiveTypeRegistry objectiveTypes = questRegistries.objective();
        objectiveTypes.register("mmoprofessionlevelup", MMOCoreProfessionObjective.class);
        objectiveTypes.register("mmochangeclass", MMOCoreChangeClassObjective.class);
        objectiveTypes.register("mmocorebreakblock", MMOCoreBreakCustomBlockObjective.class);

        final EventTypeRegistry eventTypes = questRegistries.event();
        eventTypes.register("mmoclassexperience", new MMOCoreClassExperienceEventFactory(data));
        eventTypes.register("mmoprofessionexperience", new MMOCoreProfessionExperienceEventFactory(data));
        eventTypes.register("mmocoreclasspoints", new MMOCoreClassPointsEventFactory(data));
        eventTypes.register("mmocoreattributepoints", new MMOCoreAttributePointsEventFactory(data));
        eventTypes.register("mmocoreattributereallocationpoints", new MMOCoreAttributeReallocationPointsEventFactory(data));
        eventTypes.register("mmocoreskillpoints", new MMOCoreSkillPointsEventFactory(data));
    }

    @Override
    public void reload() {
        if (mmoCoreUtils != null) {
            try {
                mmoCoreUtils.reload();
            } catch (final IOException e) {
                log.warn("Couldn't reload the MMOCore attribute configuration file!", e);
            }
        }
    }

    @Override
    public void close() {
        // Empty
    }
}
