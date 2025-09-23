package org.betonquest.betonquest.compatibility.jobsreborn;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;
import org.bukkit.plugin.Plugin;

/**
 * Factory for creating {@link JobsRebornIntegrator} instances.
 */
public class JobsRebornIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public JobsRebornIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator(final Plugin plugin) {
        return new JobsRebornIntegrator();
    }
}
