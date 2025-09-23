package org.betonquest.betonquest.compatibility.mmogroup.mmolib;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;
import org.bukkit.plugin.Plugin;

/**
 * Factory for creating {@link MythicLibIntegrator} instances.
 */
public class MythicLibIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public MythicLibIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator(final Plugin plugin) {
        return new MythicLibIntegrator();
    }
}
