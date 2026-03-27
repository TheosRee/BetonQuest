package org.betonquest.betonquest.atlas;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.integration.IntegrationService;
import org.betonquest.betonquest.api.quest.FeatureRegistry;
import org.betonquest.betonquest.atlas.atlasitemregistry.AtlasItemIntegrator;
import org.betonquest.betonquest.atlas.conversation.CustomClickSlowTellrawConvIOFactory;
import org.betonquest.betonquest.atlas.conversation.CustomClickTellrawConvIOFactory;
import org.betonquest.betonquest.atlas.objective.PlaceholderObjectiveFactory;
import org.betonquest.betonquest.atlas.simplenpc.SimpleNPCsIntegrator;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.conversation.ConversationIOFactory;
import org.betonquest.betonquest.kernel.registry.feature.ConversationIORegistry;
import org.betonquest.betonquest.lib.integration.policy.Policies;
import org.bukkit.plugin.Plugin;

/**
 * Allows to register features for Atlas.
 */
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
        integrationService.withPolicies(Policies.requirePlugin(SimpleNPCsIntegrator.PREFIX))
                .register(plugin, SimpleNPCsIntegrator::new);
        integrationService.withPolicies(Policies.requirePlugin("AtlasItemRegistry"))
                .register(plugin, AtlasItemIntegrator::new);

        api.objectives().registry().register("placeholder", new PlaceholderObjectiveFactory());

        final BetonQuest betonQuest = BetonQuest.getInstance();
        final FeatureRegistry<ConversationIOFactory> conversationIORegistry =
                betonQuest.getComponentLoader().get(ConversationIORegistry.class);
        final ConversationColors colors = betonQuest.getConversationColors();
        conversationIORegistry.register("tellraw", new CustomClickTellrawConvIOFactory(colors));
        conversationIORegistry.register("slowtellraw", new CustomClickSlowTellrawConvIOFactory(api.fonts(), colors));
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
