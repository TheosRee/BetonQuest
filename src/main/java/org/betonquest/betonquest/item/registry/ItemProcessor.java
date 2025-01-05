package org.betonquest.betonquest.item.registry;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.item.typehandler.BookHandler;
import org.betonquest.betonquest.item.typehandler.ColorHandler;
import org.betonquest.betonquest.item.typehandler.CustomModelDataHandler;
import org.betonquest.betonquest.item.typehandler.DurabilityHandler;
import org.betonquest.betonquest.item.typehandler.EnchantmentsHandler;
import org.betonquest.betonquest.item.typehandler.FireworkHandler;
import org.betonquest.betonquest.item.typehandler.FlagHandler;
import org.betonquest.betonquest.item.typehandler.HeadHandler;
import org.betonquest.betonquest.item.typehandler.ItemMetaHandler;
import org.betonquest.betonquest.item.typehandler.LoreHandler;
import org.betonquest.betonquest.item.typehandler.NameHandler;
import org.betonquest.betonquest.item.typehandler.PotionHandler;
import org.betonquest.betonquest.item.typehandler.UnbreakableHandler;
import org.betonquest.betonquest.quest.registry.processor.TypedQuestProcessor;
import org.betonquest.betonquest.quest.registry.type.TypeFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Stores QuestItems and generates new.
 */
public class ItemProcessor extends TypedQuestProcessor<ItemID, QuestItem> {

    /**
     * Static Handlers for the {@link #itemToString(ItemStack)} method.
     */
    private static final List<ItemMetaHandler<? extends ItemMeta>> STATIC_HANDLERS = List.of(
            new DurabilityHandler(), new NameHandler(), new LoreHandler(), new EnchantmentsHandler(),
            new BookHandler(), new PotionHandler(), new ColorHandler(), HeadHandler.getServerInstance(),
            new FireworkHandler(), new UnbreakableHandler(), new CustomModelDataHandler(), new FlagHandler()
    );

    /**
     * Instruction requires a QuestPackage.
     */
    private final QuestPackage dummy = new QuestDummy();

    /**
     * Create a new ItemProcessor to store and get {@link QuestItem}s.
     *
     * @param log   the custom logger for this class
     * @param types the available types
     */
    public ItemProcessor(final BetonQuestLogger log, final ItemTypeRegistry types) {
        super(log, types, "Quest Item", "items");
        types.setDefaultItemFactory(new QuestItemFactory());
    }

    /**
     * Converts ItemStack to string, which can be later parsed by QuestItem.
     *
     * @param item ItemStack to convert
     * @return converted string
     */
    public static String itemToString(final ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item.getType().toString();
        }

        final StringBuilder builder = new StringBuilder();
        for (final ItemMetaHandler<? extends ItemMeta> staticHandler : STATIC_HANDLERS) {
            final String serialize = staticHandler.rawSerializeToString(meta);
            if (serialize != null) {
                builder.append(' ').append(serialize);
            }
        }

        return item.getType() + builder.toString();
    }

    @Override
    protected ItemID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new ItemID(pack, identifier);
    }

    /**
     * Gets a QuestItem by their id.
     *
     * @param itemId the id
     * @return the stored quest item
     * @throws QuestException if there exists no QuestItem with that id
     */
    public QuestItem getItem(final ItemID itemId) throws QuestException {
        final QuestItem item = values.get(itemId);
        if (item == null) {
            throw new QuestException("Tried to load item '" + itemId
                    + "' but it is not loaded! Check for errors on /bq reload!");
        }
        return item;
    }

    /**
     * Generates a QuestItem just from instruction string.
     *
     * @param instruction the instruction string to parse
     * @return the new parsed QuestItem
     * @throws QuestException if the instruction cannot be parsed or Item could not be generated
     */
    public QuestItem generate(final String instruction) throws QuestException {
        final Instruction parsed = new Instruction(dummy, null, instruction);
        final String type = parsed.getPart(0);
        final TypeFactory<QuestItem> factory = types.getFactory(type);
        if (factory == null) {
            throw new QuestException("Unknown item type: " + type);
        }
        return factory.parseInstruction(parsed);
    }

    /**
     * An "empty" QuestPackage used for string generated QuestItems.
     */
    private static final class QuestDummy implements QuestPackage {
        /**
         * The empty constructor.
         */
        public QuestDummy() {
        }

        @Override
        public String getQuestPath() {
            throw new UnsupportedOperationException();
        }

        @Override
        public MultiConfiguration getConfig() {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<String> getTemplates() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasTemplate(final String templatePath) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean saveAll() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ConfigAccessor getOrCreateConfigAccessor(final String relativePath) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getRawString(final String address) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String subst(final String input) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getString(final String address) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getString(final String address, @Nullable final String def) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getFormattedString(final String address) {
            throw new UnsupportedOperationException();
        }
    }
}
