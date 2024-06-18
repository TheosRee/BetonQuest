package org.betonquest.betonquest.compatibility;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.compatibility.auraskills.AuraSkillsIntegratorFactory;
import org.betonquest.betonquest.compatibility.brewery.BreweryIntegratorFactory;
import org.betonquest.betonquest.compatibility.denizen.DenizenIntegratorFactory;
import org.betonquest.betonquest.compatibility.effectlib.EffectLibIntegratorFactory;
import org.betonquest.betonquest.compatibility.fabled.FabledIntegratorFactory;
import org.betonquest.betonquest.compatibility.fakeblock.FakeBlockIntegratorFactory;
import org.betonquest.betonquest.compatibility.heroes.HeroesIntegratorFactory;
import org.betonquest.betonquest.compatibility.holograms.HologramIntegrator;
import org.betonquest.betonquest.compatibility.holograms.HologramProvider;
import org.betonquest.betonquest.compatibility.holograms.decentholograms.DecentHologramsIntegratorFactory;
import org.betonquest.betonquest.compatibility.holograms.holographicdisplays.HolographicDisplaysIntegratorFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.JobsRebornIntegratorFactory;
import org.betonquest.betonquest.compatibility.luckperms.LuckPermsIntegratorFactory;
import org.betonquest.betonquest.compatibility.magic.MagicIntegratorFactory;
import org.betonquest.betonquest.compatibility.mcmmo.McMMOIntegratorFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.MMOCoreIntegratorFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.MMOItemsIntegratorFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmolib.MythicLibIntegratorFactory;
import org.betonquest.betonquest.compatibility.mythicmobs.MythicMobsIntegratorFactory;
import org.betonquest.betonquest.compatibility.npc.citizens.CitizensIntegratorFactory;
import org.betonquest.betonquest.compatibility.npc.fancynpcs.FancyNpcsIntegrator;
import org.betonquest.betonquest.compatibility.npc.fancynpcs.FancyNpcsIntegratorFactory;
import org.betonquest.betonquest.compatibility.npc.znpcsplus.ZNPCsPlusIntegrator;
import org.betonquest.betonquest.compatibility.npc.znpcsplus.ZNPCsPlusIntegratorFactory;
import org.betonquest.betonquest.compatibility.placeholderapi.PlaceholderAPIIntegratorFactory;
import org.betonquest.betonquest.compatibility.protocollib.ProtocolLibIntegratorFactory;
import org.betonquest.betonquest.compatibility.quests.QuestsIntegratorFactory;
import org.betonquest.betonquest.compatibility.redischat.RedisChatIntegratorFactory;
import org.betonquest.betonquest.compatibility.shopkeepers.ShopkeepersIntegratorFactory;
import org.betonquest.betonquest.compatibility.skript.SkriptIntegratorFactory;
import org.betonquest.betonquest.compatibility.traincarts.TrainCartsIntegratorFactory;
import org.betonquest.betonquest.compatibility.vault.VaultIntegratorFactory;
import org.betonquest.betonquest.compatibility.worldedit.WorldEditIntegratorFactory;
import org.betonquest.betonquest.compatibility.worldguard.WorldGuardIntegratorFactory;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Loads compatibility with other plugins.
 */
public class Compatibility implements Listener {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * BetonQuest API.
     */
    private final BetonQuestApi betonQuestApi;

    /**
     * Config for checking if an Integrator should be activated.
     */
    private final ConfigAccessor config;

    /**
     * Version to use when a hook error message.
     */
    private final String version;

    /**
     * A map of all integrators.
     * The key is the name of the plugin, the value a list of pairs of the integrator factory and instance from it.
     * The instance must only exist if the plugin was hooked.
     */
    private final Map<String, IntegrationTarget> integrators = new TreeMap<>();

    /**
     * The instance of the HologramProvider.
     */
    @Nullable
    private HologramProvider hologramProvider;

    /**
     * Loads all compatibility with other plugins that is available in the current runtime.
     *
     * @param log           the custom logger for this class
     * @param config        the config to check if an Integrator should be activated/hooked
     * @param betonQuestApi the BetonQuest API used to hook plugins
     * @param version       the plugin version used in error messages
     */
    public Compatibility(final BetonQuestLogger log, final BetonQuestApi betonQuestApi, final ConfigAccessor config,
                         final String version) {
        this.log = log;
        this.betonQuestApi = betonQuestApi;
        this.config = config;
        this.version = version;

        registerCompatiblePlugins();

        addExternalHooks();

        // Integrate already enabled plugins
        for (final Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            integratePlugin(plugin);
        }
    }

    /**
     * Gets the list of hooked plugins in Alphabetical order.
     *
     * @return the list of hooked plugins
     */
    public List<String> getHooked() {
        return integrators.entrySet().stream().filter(entry -> entry.getValue().integrated)
                .map(Map.Entry::getKey).toList();
        // TODO sort per supplying plugin
    }

    /**
     * After all integrations are successfully hooked,
     * this method can be called to activate cross compatibility features.
     */
    public void postHook() {
        final String hooks = String.join(", ", getHooked());
        if (!hooks.isEmpty()) {
            log.info("Enabled compatibility for " + hooks + "!");
        }
        final List<HologramIntegrator> hologramIntegrators = new ArrayList<>();
        integrators.values().forEach(target -> target.dataList.stream()
                .filter(data -> data.integrator != null)
                .forEach(data -> {
                    final Integrator integrator = data.integrator;
                    try {
                        integrator.postHook();
                        if (integrator instanceof final HologramIntegrator hologramIntegrator) {
                            hologramIntegrators.add(hologramIntegrator);
                        }
                    } catch (final HookException e) {
                        final String source = data.source == null ? "" : " ( from " + data.source.getName() + ") ";
                        log.warn("Error while enabling some features while post hooking into " + target.name
                                + source + " reason: " + e.getMessage(), e);
                    }
                }));
        hologramProvider = new HologramProvider(hologramIntegrators);
        hologramProvider.hook(betonQuestApi);
    }

    /**
     * Reloads all loaded integrators.
     */
    public void reload() {
        integrators.values().forEach(target -> target.dataList.stream()
                .map(integrationData -> integrationData.integrator)
                .filter(Objects::nonNull)
                .forEach(Integrator::reload));
        if (hologramProvider != null) {
            hologramProvider.reload();
        }
    }

    /**
     * Disables all loaded integrators.
     */
    public void disable() {
        integrators.values().forEach(integrationTarget -> integrationTarget.dataList.stream()
                .map(integrationData -> integrationData.integrator)
                .filter(Objects::nonNull)
                .forEach(Integrator::close));
        if (hologramProvider != null) {
            hologramProvider.close();
        }
    }

    private void addExternalHooks() {
        log.debug("Adding external integrators…");
        ExternalHooks.getINTEGRATORS().forEach((name, list) -> list.forEach(pair -> {
            log.debug("Receiving new hook for " + name + " from " + pair.getValue().getName());
            integrators.computeIfAbsent(name, IntegrationTarget::new).dataList
                    .add(new IntegrationData(pair.getValue(), pair.getKey()));
        }));
    }

    /**
     * Triggers the integration of a plugin.
     *
     * @param event the event to listen for
     */
    @EventHandler(ignoreCancelled = true)
    public void onPluginEnable(final PluginEnableEvent event) {
        integratePlugin(event.getPlugin());
    }

    private void integratePlugin(final Plugin hookedPlugin) {
        if (!hookedPlugin.isEnabled()) {
            return;
        }
        final String name = hookedPlugin.getName();
        final IntegrationTarget list = integrators.get(name);
        if (list == null || list.integrated) {
            return;
        }

        final boolean isEnabled = config.getBoolean("hook." + name.toLowerCase(Locale.ROOT), true);
        if (!isEnabled) {
            log.debug("Did not hook " + name + " because it is disabled");
            return;
        }

        log.info("Hooking into " + name);
        list.dataList.forEach(integrationData -> integrate(hookedPlugin, name, integrationData));
        list.integrated = true;
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private void integrate(final Plugin hookedPlugin, final String name, final IntegrationData data) {
        try {
            final Integrator integrator = data.integratorFactory.getIntegrator();
            integrator.hook(betonQuestApi);
            data.integrator = integrator;
        } catch (final HookException exception) {
            final String message = String.format("Could not hook into %s %s! %s",
                    hookedPlugin.getName(),
                    hookedPlugin.getDescription().getVersion(),
                    exception.getMessage());
            log.warn(message, exception);
            log.warn("BetonQuest will work correctly, except for that single integration. "
                    + "You can turn it off by setting 'hook." + name.toLowerCase(Locale.ROOT)
                    + "' to false in config.yml file.");
        } catch (final RuntimeException | LinkageError exception) {
            final String message = String.format("There was an unexpected error while hooking into %s %s (BetonQuest %s, Server %s)! %s",
                    hookedPlugin.getName(),
                    hookedPlugin.getDescription().getVersion(),
                    version,
                    Bukkit.getVersion(),
                    exception.getMessage());
            log.error(message, exception);
            log.warn("BetonQuest will work correctly, except for that single integration. "
                    + "You can turn it off by setting 'hook." + name.toLowerCase(Locale.ROOT)
                    + "' to false in config.yml file.");
        }
    }

    private void registerCompatiblePlugins() {
        register("MythicMobs", new MythicMobsIntegratorFactory(this));
        register("Citizens", new CitizensIntegratorFactory(this));
        register("Vault", new VaultIntegratorFactory());
        register("Skript", new SkriptIntegratorFactory());
        register("WorldGuard", new WorldGuardIntegratorFactory());
        register("WorldEdit", new WorldEditIntegratorFactory());
        register("FastAsyncWorldEdit", new WorldEditIntegratorFactory());
        register("mcMMO", new McMMOIntegratorFactory());
        register("MythicLib", new MythicLibIntegratorFactory());
        register("MMOCore", new MMOCoreIntegratorFactory());
        register("MMOItems", new MMOItemsIntegratorFactory());
        register("EffectLib", new EffectLibIntegratorFactory());
        register("Heroes", new HeroesIntegratorFactory());
        register("Magic", new MagicIntegratorFactory());
        register("Denizen", new DenizenIntegratorFactory());
        register("Fabled", new FabledIntegratorFactory());
        register("Quests", new QuestsIntegratorFactory());
        register("Shopkeepers", new ShopkeepersIntegratorFactory());
        register("PlaceholderAPI", new PlaceholderAPIIntegratorFactory());
        register("ProtocolLib", new ProtocolLibIntegratorFactory());
        register("Brewery", new BreweryIntegratorFactory());
        register("BreweryX", new BreweryIntegratorFactory());
        register("Jobs", new JobsRebornIntegratorFactory());
        register("LuckPerms", new LuckPermsIntegratorFactory());
        register("AuraSkills", new AuraSkillsIntegratorFactory());
        register("DecentHolograms", new DecentHologramsIntegratorFactory(betonQuestApi.getQuestPackageManager()));
        register("HolographicDisplays", new HolographicDisplaysIntegratorFactory(betonQuestApi.getQuestPackageManager()));
        register("fake-block", new FakeBlockIntegratorFactory());
        register("RedisChat", new RedisChatIntegratorFactory());
        register("Train_Carts", new TrainCartsIntegratorFactory());
        register(FancyNpcsIntegrator.PREFIX, new FancyNpcsIntegratorFactory());
        register(ZNPCsPlusIntegrator.PREFIX, new ZNPCsPlusIntegratorFactory());
    }

    private void register(final String name, final IntegratorFactory integrator) {
        integrators.computeIfAbsent(name, IntegrationTarget::new).dataList
                .add(new IntegrationData(null, integrator));
    }

    /**
     * Holds integration for a single plugin.
     */
    private static final class IntegrationTarget {
        /**
         * List of integrations for the plugin.
         */
        private final List<IntegrationData> dataList = new ArrayList<>();

        /**
         * Name of the target plugin.
         */
        private final String name;

        /**
         * If the plugin was already integrated.
         */
        private boolean integrated;

        private IntegrationTarget(final String name) {
            this.name = name;
        }
    }

    /**
     * Data for a specific integration of a plugin.
     */
    private static final class IntegrationData {
        /**
         * The source plugin, null if BetonQuest.
         */
        @Nullable
        private final Plugin source;

        /**
         * The factory to create a new Integration.
         */
        private final IntegratorFactory integratorFactory;

        /**
         * The created Integrator.
         */
        @Nullable
        private Integrator integrator;

        private IntegrationData(@Nullable final Plugin source, final IntegratorFactory integratorFactory) {
            this.source = source;
            this.integratorFactory = integratorFactory;
        }
    }
}
