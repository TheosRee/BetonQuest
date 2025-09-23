package org.betonquest.betonquest.compatibility.npc.fancynpcs;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;
import org.bukkit.plugin.Plugin;

/**
 * Factory for creating {@link FancyNpcsIntegrator} instances.
 */
public class FancyNpcsIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public FancyNpcsIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator(final Plugin plugin) {
        return new FancyNpcsIntegrator();
    }
}
