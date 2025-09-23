package org.betonquest.betonquest.compatibility.magic;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;
import org.bukkit.plugin.Plugin;

/**
 * Factory for creating {@link MagicIntegrator} instances.
 */
public class MagicIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public MagicIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator(final Plugin plugin) {
        return new MagicIntegrator();
    }
}
