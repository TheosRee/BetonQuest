package org.betonquest.betonquest.item;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
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
import org.betonquest.betonquest.kernel.registry.TypeFactory;
import org.betonquest.betonquest.util.BlockSelector;
import org.betonquest.betonquest.util.Utils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Creates a standard {@link BetonQuestItem} QuestItem from Instruction.
 */
public class QuestItemFactory implements TypeFactory<QuestItem> {
    /**
     * Static Handlers for the {@link #itemToString(ItemStack)} method.
     */
    private static final List<ItemMetaHandler<? extends ItemMeta>> STATIC_HANDLERS = List.of(
            new DurabilityHandler(), new NameHandler(), new LoreHandler(), new EnchantmentsHandler(),
            new BookHandler(), new PotionHandler(), new ColorHandler(), new HeadHandler(),
            new FireworkHandler(), new UnbreakableHandler(), new CustomModelDataHandler(), new FlagHandler()
    );

    /**
     * Creates a new standard Quest Item Factory.
     */
    public QuestItemFactory() {
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
    public QuestItem parseInstruction(final Instruction instruction) throws QuestException {
        final BlockSelector selector = new BlockSelector(instruction.getPart(0));

        final NameHandler name = new NameHandler();
        final LoreHandler lore = new LoreHandler();

        final List<ItemMetaHandler<?>> handlers = List.of(
                new DurabilityHandler(),
                new CustomModelDataHandler(),
                new UnbreakableHandler(),
                new FlagHandler(),
                name,
                lore,
                new EnchantmentsHandler(),
                new PotionHandler(),
                new BookHandler(),
                new HeadHandler(),
                new ColorHandler(),
                new FireworkHandler()
        );

        fillHandler(handlers, instruction.getValueParts());
        return new BetonQuestItem(selector, handlers, name, lore);
    }

    private void fillHandler(final List<ItemMetaHandler<?>> handlers, final List<String> parts) throws QuestException {
        final Map<String, ItemMetaHandler<?>> keyToHandler = new HashMap<>();
        for (final ItemMetaHandler<?> handler : handlers) {
            for (final String key : handler.keys()) {
                keyToHandler.put(key, handler);
            }
        }

        for (final String part : parts) {
            if (part.isEmpty()) {
                continue; //catch empty string caused by multiple whitespaces in instruction split
            }

            final String argumentName = getArgumentName(part.toLowerCase(Locale.ROOT));
            final String data = getArgumentData(part);

            final ItemMetaHandler<?> handler = Utils.getNN(keyToHandler.get(argumentName), "Unknown argument: " + argumentName);
            handler.set(argumentName, data);
        }
    }

    /**
     * Returns the data behind the argument name.
     * If the argument does not contain a colon, it returns the full argument.
     *
     * @param argument the full argument
     * @return the data behind the argument name
     */
    private String getArgumentData(final String argument) {
        return argument.substring(argument.indexOf(':') + 1);
    }

    /**
     * Returns the argument name.
     * If the argument does not contain a colon, it returns the full argument.
     *
     * @param argument the full argument
     * @return the argument name
     */
    private String getArgumentName(final String argument) {
        if (argument.contains(":")) {
            return argument.substring(0, argument.indexOf(':'));
        }
        return argument;
    }
}
