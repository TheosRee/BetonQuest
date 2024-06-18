package org.betonquest.betonquest.compatibility;

import org.bukkit.plugin.Plugin;

/**
 * Allows to add integrators into the BetonQuest hook system.
 */
public interface Integrations {

    /**
     * Registers a new IntegratorFactory targeting a plugin.
     *
     * @param name       the name of the plugin to hook
     * @param integrator the integrator factory providing functionality
     * @param plugin     the plugin registering the factory
     */
    void registerPlugin(String name, IntegratorFactory integrator, Plugin plugin);

    /**
     * Registers a new IntegratorFactory targeting a minimum Minecraft version.
     *
     * @param version    the required minecraft version to hook
     * @param integrator the integrator factory providing functionality
     * @param plugin     the plugin registering the factory
     */
    void registerVanilla(String version, IntegratorFactory integrator, Plugin plugin);
}
