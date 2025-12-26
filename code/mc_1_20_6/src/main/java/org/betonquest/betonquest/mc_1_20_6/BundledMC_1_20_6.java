package org.betonquest.betonquest.mc_1_20_6;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.common.component.BookPageWrapper;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.item.ItemRegistry;
import org.betonquest.betonquest.mc_1_20_6.item.UpdatedSimpleItemFactory;
import org.betonquest.betonquest.mc_1_20_6.item.UpdatedSimpleQuestItemSerializer;

/**
 * Allows to register features with Minecraft 1.20.6.
 */
@SuppressWarnings("PMD.ClassNamingConventions")
public class BundledMC_1_20_6 implements Integrator {

    /**
     * Custom Logger instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * BetonQuest class to get relevant object from.
     */
    private final BetonQuest betonQuest;

    /**
     * Creates a new Object to register Minecraft version changes.
     *
     * @param log        the custom logger for this class
     * @param betonQuest the BetonQuest class to get relevant object from
     */
    public BundledMC_1_20_6(final BetonQuestLogger log, final BetonQuest betonQuest) {
        this.log = log;
        this.betonQuest = betonQuest;
    }

    @Override
    public void hook(final BetonQuestApi api) {
        final ItemRegistry item = betonQuest.getFeatureRegistries().item();
        final TextParser textParser = betonQuest.getTextParser();
        final BookPageWrapper bookPageWrapper = new BookPageWrapper(betonQuest.getFontRegistry(), 114, 14);
        item.register("simple", new UpdatedSimpleItemFactory(betonQuest.getQuestTypeApi().variables(),
                betonQuest.getQuestPackageManager(), textParser, bookPageWrapper,
                () -> betonQuest.getPluginConfig().getBoolean("item.quest.lore") ? betonQuest.getPluginMessage() : null));
        item.registerSerializer("simple", new UpdatedSimpleQuestItemSerializer(textParser, bookPageWrapper));
        log.info("Enabled Minecraft 1.20.6 module");
    }

    @Override
    public void reload() {
        // Empty
    }

    @Override
    public void close() {
        // Empty
    }
}
