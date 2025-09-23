package org.betonquest.betonquest.compatibility.placeholderapi;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;
import org.bukkit.plugin.Plugin;

/**
 * Factory for creating {@link PlaceholderAPIIntegrator} instances.
 */
public class PlaceholderAPIIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public PlaceholderAPIIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator(final Plugin plugin) {
        return new PlaceholderAPIIntegrator();
    }
}
