package org.betonquest.betonquest.compatibility;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Allows to store the external integrations before plugin enabling.
 */
public class CompIntegrations implements Integrations {

    /**
     * New Plugin Integrators to add.
     */
    private final Map<String, List<Map.Entry<IntegratorFactory, Plugin>>> pluginIntegration = new ConcurrentHashMap<>();

    /**
     * New Vanilla Integrators to add.
     */
    private final Map<String, List<Map.Entry<IntegratorFactory, Plugin>>> vanillaIntegration = new ConcurrentHashMap<>();

    /**
     * If the compatibility already initialized and new integrators should be denied.
     */
    private boolean freeze;

    /**
     * The empty default constructor.
     */
    public CompIntegrations() {
    }

    /* default */ void init(final BetonQuestLogger log, final Compatibility compatibility, final Map<String, Compatibility.BaseIntegrationSource> external) {
        freeze = true;
        log.debug("Adding external integrators…");
        pluginIntegration.forEach((name, list) -> list.forEach(entry -> {
            final String pluginName = entry.getValue().getName();
            log.debug("Receiving external hook for " + name + " from " + pluginName);
            final Compatibility.BaseIntegrationSource source = external.computeIfAbsent(pluginName, Compatibility.BaseIntegrationSource::new);
            compatibility.registerPlugin(name, entry.getKey(), source);
        }));
        pluginIntegration.clear();
        vanillaIntegration.forEach((version, list) -> list.forEach(entry -> {
            final String pluginName = entry.getValue().getName();
            log.debug("Receiving external hook for Minecraft " + version + " from " + pluginName);
            final Compatibility.BaseIntegrationSource source = external.computeIfAbsent(pluginName, Compatibility.BaseIntegrationSource::new);
            compatibility.registerVanilla(version, entry.getKey(), source);
        }));
        vanillaIntegration.clear();
    }

    @Override
    public void registerPlugin(final String name, final IntegratorFactory integrator, final Plugin plugin) {
        if (freeze) {
            throw new IllegalStateException("Cannot register new integrator after hooking!");
        }
        pluginIntegration.computeIfAbsent(name, ignored -> Collections.synchronizedList(new ArrayList<>()))
                .add(Map.entry(integrator, plugin));
    }

    @Override
    public void registerVanilla(final String version, final IntegratorFactory integrator, final Plugin plugin) {
        if (freeze) {
            throw new IllegalStateException("Cannot register new integrator after hooking!");
        }
        vanillaIntegration.computeIfAbsent(version, ignored -> Collections.synchronizedList(new ArrayList<>()))
                .add(Map.entry(integrator, plugin));
    }
}
