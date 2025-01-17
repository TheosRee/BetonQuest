package org.betonquest.betonquest.api.compatibility;

import org.bukkit.plugin.Plugin;

/**
 * Allows to add plugin hooks into the BetonQuest hook system.
 */
@FunctionalInterface
public interface CompatibilityRegistry {
    /**
     * Registers a new Integrator for 3rd party plugins.
     *
     * @param plugin  the plugin to hook
     * @param factory the factory creating the integrator class functionality
     */
    default void register(final Plugin plugin, final IntegratorFactory factory) {
        this.register(plugin, factory, plugin.getName());
    }

    /**
     * Registers a new Integrator for 3rd party plugins.
     *
     * @param plugin  the plugin registering the hook
     * @param factory the factory creating the integrator class functionality
     * @param name    the name of the plugin to hook
     */
    void register(Plugin plugin, IntegratorFactory factory, String name);
}
