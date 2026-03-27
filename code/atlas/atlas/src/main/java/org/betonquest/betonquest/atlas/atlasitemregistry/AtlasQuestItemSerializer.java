package org.betonquest.betonquest.atlas.atlasitemregistry;

import com.ags.atlasitemregistry.atlaslib.pdc.DataType;
import com.ags.atlasitemregistry.atlaslib.util.PDC;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.item.QuestItemSerializer;
import org.bukkit.inventory.ItemStack;

/**
 * Serializes AtlasItemRegistry items into string form.
 */
public class AtlasQuestItemSerializer implements QuestItemSerializer {

    @Override
    public String serialize(final ItemStack itemStack) throws QuestException {
        if (PDC.has(itemStack, "material")) {
            return PDC.get(itemStack, "material", DataType.STRING);
        }
        return itemStack.getType().name();
    }
}
