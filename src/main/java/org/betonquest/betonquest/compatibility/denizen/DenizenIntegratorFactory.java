package org.betonquest.betonquest.compatibility.denizen;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;
import org.bukkit.plugin.Plugin;

/**
 * Factory for creating {@link DenizenIntegrator} instances.
 */
public class DenizenIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public DenizenIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator(final Plugin plugin) {
        return new DenizenIntegrator();
    }
}
