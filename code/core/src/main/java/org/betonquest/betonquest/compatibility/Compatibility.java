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
     * BetonQuest plugin as source of integrations.
     */
    private final Plugin betonQuestPlugin;

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
     * @param log              the custom logger for this class
     * @param config           the config to check if an Integrator should be activated/hooked
     * @param betonQuestApi    the BetonQuest API used to hook plugins
     * @param version          the plugin version used in error messages
     * @param betonQuestPlugin the BetonQuest plugin as source of integrations
     */
    public Compatibility(final BetonQuestLogger log, final BetonQuestApi betonQuestApi, final ConfigAccessor config,
                         final String version, final Plugin betonQuestPlugin) {
        this.log = log;
        this.betonQuestApi = betonQuestApi;
        this.config = config;
        this.version = version;
        this.betonQuestPlugin = betonQuestPlugin;
    }

    /**
     * Integrate plugins.
     */
    public void init() {
        addExternalHooks();
        for (final Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            integratePlugin(plugin);
        }
        postHook();
    }

    /**
     * Gets the list of hooked plugins in Alphabetical order.
     *
     * @return the list of hooked plugins
     */
    public List<String> getHooked() {
        return integrators.values().stream()
                .filter(target -> target.integrated)
                .map(Object::toString)
                .toList();
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
                        final String source = betonQuestPlugin.equals(data.source) ? "" : " ( from " + data.source.getName() + ") ";
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

    /**
     * Adds a new Integrator Factory for a Plugin.
     *
     * @param name       the plugin name
     * @param integrator the integrator factory
     */
    public void register(final String name, final IntegratorFactory integrator) {
        register(name, integrator, betonQuestPlugin);
    }

    private void register(final String name, final IntegratorFactory integrator, final Plugin source) {
        integrators.computeIfAbsent(name, IntegrationTarget::new).dataList
                .add(new IntegrationData(source, integrator));
    }

    private void addExternalHooks() {
        log.debug("Adding external integrators…");
        ExternalHooks.getINTEGRATORS().forEach((name, list) -> list.forEach(pair -> {
            log.debug("Receiving new hook for " + name + " from " + pair.getValue().getName());
            register(name, pair.getKey(), pair.getValue());
        }));
        ExternalHooks.getINTEGRATORS().clear();
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

        @Override
        public String toString() {
            if (dataList.size() == 1 && "BetonQuest".equals(dataList.get(0).source.getName())) {
                return name;
            }
            return name + "(" + dataList.stream().map(data -> data.source.getName())
                    .collect(Collectors.joining(", ")) + ")";
        }
    }

    /**
     * Data for a specific integration of a plugin.
     */
    private static final class IntegrationData {

        /**
         * The source plugin.
         */
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

        private IntegrationData(final Plugin source, final IntegratorFactory integratorFactory) {
            this.source = source;
            this.integratorFactory = integratorFactory;
        }
    }
}
