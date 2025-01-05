package org.betonquest.betonquest.item.registry;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.item.BetonQuestItem;
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
import org.betonquest.betonquest.quest.registry.type.TypeFactory;
import org.betonquest.betonquest.util.BlockSelector;
import org.betonquest.betonquest.util.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Creates a standard {@link BetonQuestItem} QuestItem from Instruction.
 */
public class QuestItemFactory implements TypeFactory<QuestItem> {
    /**
     * Creates a new standard Quest Item Factory.
     */
    public QuestItemFactory() {
    }

    @Override
    public QuestItem parseInstruction(final Instruction instruction) throws QuestException {
        final BlockSelector selector = new BlockSelector(instruction.getPart(0));

        final DurabilityHandler durability = new DurabilityHandler();
        final NameHandler name = new NameHandler();
        final LoreHandler lore = new LoreHandler();
        final EnchantmentsHandler enchants = new EnchantmentsHandler();
        final UnbreakableHandler unbreakable = new UnbreakableHandler();
        final PotionHandler potion = new PotionHandler();
        final BookHandler book = new BookHandler();
        final HeadHandler head = HeadHandler.getServerInstance();
        final ColorHandler color = new ColorHandler();
        final FireworkHandler firework = new FireworkHandler();
        final CustomModelDataHandler customModelData = new CustomModelDataHandler();
        final FlagHandler flags = new FlagHandler();

        final List<ItemMetaHandler<?>> handlers = List.of(
                durability, customModelData, unbreakable, flags, name, lore,
                enchants, potion, book, head, color, firework
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
