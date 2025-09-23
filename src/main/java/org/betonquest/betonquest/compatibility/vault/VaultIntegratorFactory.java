package org.betonquest.betonquest.compatibility.vault;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;
import org.bukkit.plugin.Plugin;

/**
 * Factory for creating {@link VaultIntegrator} instances.
 */
public class VaultIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public VaultIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator(final Plugin plugin) {
        return new VaultIntegrator();
    }
}
