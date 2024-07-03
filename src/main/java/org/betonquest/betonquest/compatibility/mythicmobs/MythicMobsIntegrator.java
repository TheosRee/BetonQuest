package org.betonquest.betonquest.compatibility.mythicmobs;

import io.lumine.mythic.bukkit.BukkitAPIHelper;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.mythicmobs.conditions.MythicMobDistanceConditionFactory;
import org.betonquest.betonquest.compatibility.mythicmobs.events.MythicSpawnMobEventFactory;
import org.betonquest.betonquest.compatibility.mythicmobs.npc.MMBQAdapter;
import org.betonquest.betonquest.compatibility.mythicmobs.npc.MMConversationStarter;
import org.betonquest.betonquest.compatibility.mythicmobs.npc.objectives.MMInteractObjective;
import org.betonquest.betonquest.compatibility.mythicmobs.npc.objectives.MMRangeObjective;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCIntegrator;
import org.betonquest.betonquest.compatibility.protocollib.hider.MythicHider;
import org.betonquest.betonquest.exceptions.HookException;
import org.betonquest.betonquest.exceptions.UnsupportedVersionException;
import org.betonquest.betonquest.modules.versioning.UpdateStrategy;
import org.betonquest.betonquest.modules.versioning.Version;
import org.betonquest.betonquest.modules.versioning.VersionComparator;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.registry.QuestTypeRegistries;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * Integrator for <a href="https://git.mythiccraft.io/mythiccraft/MythicMobs/-/wikis/API">MythicMobs</a>.
 */
public class MythicMobsIntegrator extends NPCIntegrator<ActiveMob> {
    /**
     * The default Constructor.
     */
    public MythicMobsIntegrator() {

    }

    /**
     * Gets a MythicMobs BQ NPC Adapter from its UUID.
     *
     * @param npcId the {@link UUID} as string
     * @return the supplier for new NPC Wrapper
     */
    public static Supplier<BQNPCAdapter<?>> getSupplierByIDStatic(final String npcId) {
        return () -> {
            final Entity entity = Bukkit.getEntity(UUID.fromString(npcId));
            if (entity == null) {
                return null;
            }
            final ActiveMob activeMob = new BukkitAPIHelper().getMythicMobInstance(entity);
            return activeMob == null ? null : new MMBQAdapter(activeMob);
        };
    }

    @Override
    public void hook() throws HookException {
        validateVersion();

        final BukkitAPIHelper apiHelper = new BukkitAPIHelper();

        final Server server = plugin.getServer();
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);
        final QuestTypeRegistries questRegistries = plugin.getQuestRegistries();
        questRegistries.getConditionTypes().register("mythicmobdistance", new MythicMobDistanceConditionFactory(apiHelper, data));
        plugin.registerObjectives("mmobkill", MythicMobKillObjective.class);
        questRegistries.getEventTypes().registerCombined("mspawnmob", new MythicSpawnMobEventFactory(apiHelper, data, plugin.getVariableProcessor()));
        if (Compatibility.getHooked().contains("ProtocolLib")) {
            MythicHider.start();
        }
        
        hook("mm", () -> MythicMobsIntegrator::getSupplierByIDStatic,
                loggerFactory -> new MMConversationStarter(loggerFactory, loggerFactory.create(MMConversationStarter.class)),
                MMInteractObjective.class, MMRangeObjective.class);
    }

    /**
     * Aborts the hooking process if the installed version of MythicMobs is invalid.
     *
     * @throws UnsupportedVersionException if the installed version of MythicMobs is < 5.0.0.
     */
    private void validateVersion() throws UnsupportedVersionException {
        final Plugin mythicMobs = Bukkit.getPluginManager().getPlugin("MythicMobs");
        final String versionWithCommit = mythicMobs.getDescription().getVersion();
        final String[] parts = versionWithCommit.split("-");
        final Version mythicMobsVersion = new Version(parts[0]);
        final VersionComparator comparator = new VersionComparator(UpdateStrategy.MAJOR, "-");
        if (comparator.isOtherNewerThanCurrent(mythicMobsVersion, new Version("5.0.0"))) {
            throw new UnsupportedVersionException(mythicMobs, "5.0.0+");
        }
    }
}
