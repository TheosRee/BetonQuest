package org.betonquest.betonquest.compatibility;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Allows to add plugin hooks into the BetonQuest hook system.
 */
public final class ExternalHooks {

    /**
     * New Integrators to add.
     */
    private static final Map<String, List<Map.Entry<IntegratorFactory, Plugin>>> INTEGRATORS = new ConcurrentHashMap<>();

    /**
     * New Vanilla Integrators to add.
     */
    private static final Map<String, List<Map.Entry<IntegratorFactory, Plugin>>> VANILLA = new ConcurrentHashMap<>();

    /**
     * If the compatibility already initialized and new entries should be denied.
     */
    private static final AtomicBoolean FREEZE = new AtomicBoolean(false);

    /**
     * The private constructor.
     */
    private ExternalHooks() {
    }

    /**
     * Registers a new IntegratorFactory.
     *
     * @param name       the name of the plugin to hook
     * @param integrator the integrator factory providing functionality
     * @param plugin     the plugin registering the hook
     */
    public static void register(final String name, final IntegratorFactory integrator, final Plugin plugin) {
        if (FREEZE.get()) {
            throw new IllegalStateException("Cannot register new compatibility after plugin enabling!");
        }
        INTEGRATORS.computeIfAbsent(name, ignored -> Collections.synchronizedList(new ArrayList<>()))
                .add(Map.entry(integrator, plugin));
    }

    /**
     * Registers a new IntegratorFactory.
     *
     * @param version    the required minecraft version of the plugin to hook
     * @param integrator the integrator factory providing functionality
     * @param plugin     the plugin registering the hook
     */
    public static void registerVanilla(final String version, final IntegratorFactory integrator, final Plugin plugin) {
        if (FREEZE.get()) {
            throw new IllegalStateException("Cannot register new compatibility after plugin enabling!");
        }
        VANILLA.computeIfAbsent(version, ignored -> Collections.synchronizedList(new ArrayList<>()))
                .add(Map.entry(integrator, plugin));
    }

    /* default */
    static Map<String, List<Map.Entry<IntegratorFactory, Plugin>>> getINTEGRATORS() {
        FREEZE.set(true);
        return INTEGRATORS;
    }

    /* default */
    static Map<String, List<Map.Entry<IntegratorFactory, Plugin>>> getVANILLA() {
        FREEZE.set(true);
        return VANILLA;
    }
}
