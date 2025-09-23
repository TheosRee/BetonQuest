package org.betonquest.betonquest.compatibility;

import org.bukkit.plugin.Plugin;

/**
 * Factory for creating {@link Integrator} instances.
 */
@FunctionalInterface
public interface IntegratorFactory {
    /**
     * Creates a new {@link Integrator} instance.
     *
     * @param plugin the plugin to integrate
     * @return a new {@link Integrator} instance
     * @throws HookException when the integrator could not be created
     *                       - for example when the plugin has the wrong version
     */
    Integrator getIntegrator(Plugin plugin) throws HookException;
}
