package org.betonquest.betonquest.compatibility.heroes;

import com.herocraftonline.heroes.Heroes;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;
import org.bukkit.plugin.Plugin;

/**
 * Factory for creating {@link HeroesIntegrator} instances.
 */
public class HeroesIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public HeroesIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator(final Plugin plugin) throws HookException {
        if (plugin instanceof Heroes) {
            return new HeroesIntegrator((Heroes) plugin);
        }
        throw new HookException(plugin, "Plugin is not Heroes!");
    }
}
