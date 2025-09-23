package org.betonquest.betonquest.compatibility.worldguard;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;
import org.bukkit.plugin.Plugin;

/**
 * Factory for creating {@link WorldGuardIntegrator} instances.
 */
public class WorldGuardIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public WorldGuardIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator(final Plugin plugin) {
        return new WorldGuardIntegrator();
    }
}
