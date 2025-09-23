package org.betonquest.betonquest.compatibility.skript;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;
import org.bukkit.plugin.Plugin;

/**
 * Factory for creating {@link SkriptIntegrator} instances.
 */
public class SkriptIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public SkriptIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator(final Plugin plugin) {
        return new SkriptIntegrator();
    }
}
