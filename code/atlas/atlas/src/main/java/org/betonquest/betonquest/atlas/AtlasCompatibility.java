package org.betonquest.betonquest.atlas;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.integration.IntegrationService;
import org.bukkit.plugin.Plugin;

/**
 * Allows to register features for Atlas.
 */
@SuppressWarnings("PMD.UnusedPrivateField")
public class AtlasCompatibility implements Integration {

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * Service instance to register the integrations to.
     */
    private final IntegrationService integrationService;

    /**
     * Creates a new AtlasCompatibility.
     *
     * @param plugin             the plugin instance
     * @param integrationService the service instance to register the integrations to
     */
    public AtlasCompatibility(final Plugin plugin, final IntegrationService integrationService) {
        this.plugin = plugin;
        this.integrationService = integrationService;
    }

    @Override
    public void enable(final BetonQuestApi api) {
        // Empty
    }

    @Override
    public void postEnable(final BetonQuestApi api) {
        // Empty
    }

    @Override
    public void disable() {
        // Empty
    }
}
