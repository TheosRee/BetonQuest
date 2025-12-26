package org.betonquest.betonquest.compatibility;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.compatibility.holograms.HologramIntegrator;
import org.betonquest.betonquest.compatibility.holograms.HologramProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Loads compatibility with other plugins.
 */
public class Compatibility {

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
    private final Map<String, IntegrationSource> integrators = new TreeMap<>();

    /**
     * A map of plugin names and their integrator factories.
     * The key is the name of the plugin, the value a list of pairs of the integrator factory and instance from it.
     * The instance must only exist if the plugin was hooked.
     */
    private final Map<String, List<IntegrationData>> dataByPlugin = new TreeMap<>();

    /**
     * BetonQuest provided integrations.
     */
    private final IntegrationSource betonSource;

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
        this.betonSource = new IntegrationSource(null);
    }

    /**
     * Integrate plugins.
     */
    public void init() {
        addExternalHooks();
        for (final Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            integratePlugin(plugin);
        }
        final String hooks = integrators.values().stream()
                .filter(IntegrationSource::shouldBeListed)
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        if (!hooks.isEmpty()) {
            log.info("Enabled compatibility for " + hooks + "!");
        }
        postHook();
    }

    /**
     * Gets the list of hooked plugins in Alphabetical order.
     *
     * @return the list of hooked plugins
     */
    public List<String> getPluginNames() {
        return dataByPlugin.entrySet().stream()
                .filter(entry -> entry.getValue().stream().anyMatch(data -> data.integrated))
                .map(Map.Entry::getKey).sorted().toList();
    }

    /**
     * Get.
     *
     * @return what?
     */
    public IntegrationSource getBetonSource() {
        return betonSource;
    }

    /**
     * Get.
     *
     * @return what?
     */
    public List<IntegrationSource> getSources() {
        return List.copyOf(integrators.values());
    }

    /**
     * After all integrations are successfully hooked,
     * this method can be called to activate cross compatibility features.
     */
    public void postHook() {
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
                        log.warn("Error while enabling some features while post hooking into " + target.name
                                + data.source.getFrom() + " reason: " + e.getMessage(), e);
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

    private void integratePlugin(final Plugin hookedPlugin) {
        if (!hookedPlugin.isEnabled()) {
            return;
        }
        final String name = hookedPlugin.getName();
        final List<IntegrationData> list = dataByPlugin.get(name);
        if (list == null || list.isEmpty()) {
            return;
        }

        final boolean isEnabled = config.getBoolean("hook." + name.toLowerCase(Locale.ROOT), true);
        if (!isEnabled) {
            log.debug("Did not hook " + name + " because it is disabled");
            return;
        }

        log.info("Hooking into " + name);
        list.forEach(integrationData -> {
            if (integrationData.integrated) {
                return;
            }
            integrate(hookedPlugin, name, integrationData);
            integrationData.integrated = true;
        });
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private void integrate(final Plugin hookedPlugin, final String name, final IntegrationData data) {
        try {
            final Integrator integrator = data.integratorFactory.getIntegrator();
            integrator.hook(betonQuestApi);
            data.integrator = integrator;
            data.target = hookedPlugin;
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

    /**
     * Adds a new Integrator Factory for a Plugin with BetonQuest as source.
     *
     * @param name       the plugin name
     * @param integrator the integrator factory
     */
    public void register(final String name, final IntegratorFactory integrator) {
        register(name, integrator, betonSource);
    }

    private void register(final String name, final IntegratorFactory integrator, final IntegrationSource source) {
        final IntegrationData data = new IntegrationData(source, integrator);
        source.dataList.add(data);
        dataByPlugin.computeIfAbsent(name, ignored -> new ArrayList<>()).add(data);
    }

    private void addExternalHooks() {
        log.debug("Adding external integrators…");
        ExternalHooks.getINTEGRATORS().forEach((name, list) -> list.forEach(pair -> {
            final String pluginName = pair.getValue().getName();
            log.debug("Loading external hook for " + name + " from " + pluginName);
            final IntegrationSource source = integrators.computeIfAbsent(pluginName, IntegrationSource::new);
            register(name, pair.getKey(), source);
        }));
        ExternalHooks.getINTEGRATORS().clear();
    }

    /**
     * Holds integration from a single plugin.
     */
    public static final class IntegrationSource {

        /**
         * List of integrations for the plugin.
         */
        private final List<IntegrationData> dataList = new ArrayList<>();

        /**
         * Name of the source plugin.
         */
        @Nullable
        private final String name;

        private IntegrationSource(@Nullable final String name) {
            this.name = name;
        }

        private boolean shouldBeListed() {
            return dataList.stream().anyMatch(data -> data.integrator != null);
        }

        /**
         * Get.
         *
         * @return what?
         */
        public String getFrom() {
            return name == null ? "" : (" (From " + name + ") ");
        }

        @Override
        public String toString() {
            return getFrom() + "Hooked into: "
                    + dataList.stream().map(Object::toString).collect(Collectors.joining(", "));
        }

        /**
         * Get.
         *
         * @return immutable list of data
         */
        public List<IntegrationData> getDataList() {
            return List.copyOf(dataList);
        }
    }

    /**
     * Data for a specific integration of a plugin.
     */
    public static final class IntegrationData {

        /**
         * Source used in stream references.
         */
        private final IntegrationSource source;

        /**
         * The factory to create a new Integration.
         */
        private final IntegratorFactory integratorFactory;

        /**
         * The target plugin, if hooked.
         */
        @Nullable
        private Plugin target;

        /**
         * If an integration was attempted. The integrator may still be null if it was not successful.
         */
        private boolean integrated;

        /**
         * The created Integrator.
         */
        @Nullable
        private Integrator integrator;

        private IntegrationData(final IntegrationSource source, final IntegratorFactory integratorFactory) {
            this.source = source;
            this.integratorFactory = integratorFactory;
        }

        /**
         * Get.
         *
         * @return what?
         */
        @Nullable
        public Plugin getTarget() {
            return target;
        }
    }
}
