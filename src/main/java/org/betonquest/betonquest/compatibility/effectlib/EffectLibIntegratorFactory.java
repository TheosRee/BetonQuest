package org.betonquest.betonquest.compatibility.effectlib;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;
import org.bukkit.plugin.Plugin;

/**
 * Factory for creating {@link EffectLibIntegrator} instances.
 */
public class EffectLibIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public EffectLibIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator(final Plugin plugin) {
        return new EffectLibIntegrator();
    }
}
