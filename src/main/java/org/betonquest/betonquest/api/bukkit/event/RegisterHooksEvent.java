package org.betonquest.betonquest.api.bukkit.event;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.compatibility.Integrator;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

import java.util.Map;

/**
 * Allows to add plugin hooks into the BetonQuest hook system.
 */
public class RegisterHooksEvent extends Event {
    /**
     * Static HandlerList to register this event in EventHandlers.
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * New Integrators to add.
     */
    private final Map<String, Pair<Class<? extends Integrator>, Integrator>> integrators;

    /**
     * Custom {@link BetonQuestLogger} logging external hook registers.
     */
    private final BetonQuestLogger log;

    /**
     * Creates a new RegisterHooksEvent with a map to register new integrators.
     *
     * @param integrators the map to add integrators to
     * @param log         the custom logger for logging registering integrators
     */
    public RegisterHooksEvent(final Map<String, Pair<Class<? extends Integrator>, Integrator>> integrators, final BetonQuestLogger log) {
        this.integrators = integrators;
        this.log = log;
    }

    /**
     * Static HandlerList to register this event in EventHandlers.
     *
     * @return the HandlerList
     */
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    /**
     * Registers a new Integrator for 3rd party plugins.
     *
     * @param name       the name of the plugin to hook
     * @param integrator the integrator class providing functionality
     * @param plugin     the plugin registering the hook
     */
    public void register(final String name, final Class<? extends Integrator> integrator, final Plugin plugin) {
        log.debug("Receiving new hook for " + name + " from " + plugin.getName());
        integrators.put(name, new MutablePair<>(integrator, null));
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
