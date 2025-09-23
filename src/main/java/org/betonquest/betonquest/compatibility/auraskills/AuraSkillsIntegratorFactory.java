package org.betonquest.betonquest.compatibility.auraskills;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;
import org.bukkit.plugin.Plugin;

/**
 * Factory for creating {@link AuraSkillsIntegrator} instances.
 */
public class AuraSkillsIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public AuraSkillsIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator(final Plugin plugin) {
        return new AuraSkillsIntegrator();
    }
}
