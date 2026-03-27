package org.betonquest.betonquest.atlas.atlasitemregistry;

import com.ags.atlasitemregistry.AtlasItemRegistryService;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.service.item.ItemRegistry;
import org.bukkit.Bukkit;

/**
 * Integrator for Atlas Items.
 */
public class AtlasItemIntegrator implements Integration {

    /**
     * The default constructor.
     */
    public AtlasItemIntegrator() {

    }

    @Override
    public void enable(final BetonQuestApi api) throws QuestException {
        final ItemRegistry itemTypes = api.items().registry();
        final AtlasItemRegistryService registry = Bukkit.getServer().getServicesManager().load(AtlasItemRegistryService.class);
        if (registry == null) {
            throw new QuestException("Registry service not loaded");
        }
        itemTypes.register("registry", new AtlasQuestItemFactory(registry));
        itemTypes.registerSerializer("registry", new AtlasQuestItemSerializer());
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
