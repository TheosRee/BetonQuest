package org.betonquest.betonquest.compatibility;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.compatibility.CompatibilityRegistry;
import org.betonquest.betonquest.api.compatibility.Integrator;
import org.betonquest.betonquest.api.compatibility.IntegratorFactory;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Registry for 3rd party Integrators which freezes when getting values.
 */
public class CompatibilityRegistryImpl implements CompatibilityRegistry {

    /**
     * New Integrators to add.
     */
    private final Map<String, Pair<IntegratorFactory, Integrator>> integrators;

    /**
     * ~Custom {@link BetonQuestLogger} logging external hook registers.~ A normal logger.
     */
    private final Logger log;

    /**
     * If the compatibility already loaded and new entries should be denied.
     */
    private boolean freeze;

    /**
     * Creates a new RegisterHooksEvent to register new integrators.
     *
     * @param log the custom logger for logging registering integrators
     */
    public CompatibilityRegistryImpl(final Logger log) {
        this.integrators = new HashMap<>();
        this.log = log;
    }

    @Override
    public void register(final Plugin plugin, final IntegratorFactory factory, final String name) {
        if (freeze) {
            throw new IllegalStateException("Cannot register new compatibility after compatibility freeze!");
        }
        log.fine("Receiving new hook for " + name + " from " + plugin.getName());
        integrators.put(name, new MutablePair<>(factory, null));
    }

    /**
     * Gets the integrators and freezes.
     *
     * @return added integrators
     */
    public Map<String, Pair<IntegratorFactory, Integrator>> getIntegrators() {
        freeze = true;
        return integrators;
    }
}
