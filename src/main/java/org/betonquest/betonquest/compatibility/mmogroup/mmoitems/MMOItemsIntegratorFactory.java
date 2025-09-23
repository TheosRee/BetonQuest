package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;
import org.bukkit.plugin.Plugin;

/**
 * Factory for creating {@link MMOItemsIntegrator} instances.
 */
public class MMOItemsIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public MMOItemsIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator(final Plugin plugin) {
        return new MMOItemsIntegrator();
    }
}
