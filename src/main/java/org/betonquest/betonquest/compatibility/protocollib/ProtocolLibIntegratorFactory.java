package org.betonquest.betonquest.compatibility.protocollib;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;
import org.bukkit.plugin.Plugin;

/**
 * Factory for creating {@link ProtocolLibIntegrator} instances.
 */
public class ProtocolLibIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public ProtocolLibIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator(final Plugin plugin) {
        return new ProtocolLibIntegrator();
    }
}
