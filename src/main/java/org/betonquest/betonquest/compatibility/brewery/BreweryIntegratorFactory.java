package org.betonquest.betonquest.compatibility.brewery;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;
import org.bukkit.plugin.Plugin;

/**
 * Factory for creating {@link BreweryIntegrator} instances.
 */
public class BreweryIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public BreweryIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator(final Plugin plugin) {
        return new BreweryIntegrator();
    }
}
