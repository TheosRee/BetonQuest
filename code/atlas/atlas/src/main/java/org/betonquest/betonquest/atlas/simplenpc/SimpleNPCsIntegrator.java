package org.betonquest.betonquest.atlas.simplenpc;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.service.npc.NpcRegistry;
import org.betonquest.betonquest.kernel.processor.quest.NpcProcessor;

/**
 * Integrator implementation for the FancyNpcs plugin.
 */
public class SimpleNPCsIntegrator implements Integration {

    /**
     * The prefix used before any registered name for distinguishing.
     */
    public static final String PREFIX = "SimpleNPCs";

    /**
     * The empty default Constructor.
     */
    public SimpleNPCsIntegrator() {
    }

    @Override
    public void enable(final BetonQuestApi api) {
        final NpcRegistry npcRegistry = api.npcs().registry();
        api.bukkit().registerEvents(new SimpleCatcher(api.profiles(), npcRegistry));
        final SimpleHider hider = new SimpleHider(BetonQuest.getInstance().getComponentLoader().get(NpcProcessor.class).getNpcHider());
        api.bukkit().registerEvents(hider);
        npcRegistry.register(PREFIX, new SimpleFactory());
        npcRegistry.registerIdentifier(new SimpleIdentifier(PREFIX));
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
