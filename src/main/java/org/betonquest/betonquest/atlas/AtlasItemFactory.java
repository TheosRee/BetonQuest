package org.betonquest.betonquest.atlas;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.kernel.registry.TypeFactory;

public class AtlasItemFactory implements TypeFactory<QuestItem> {
    @Override
    public QuestItem parseInstruction(final Instruction instruction) throws QuestException {
        return new AtlasItem(instruction.next());
    }
}
