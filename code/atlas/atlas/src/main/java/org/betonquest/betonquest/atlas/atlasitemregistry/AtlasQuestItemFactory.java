package org.betonquest.betonquest.atlas.atlasitemregistry;

import com.ags.atlasitemregistry.AtlasItemRegistryService;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.item.QuestItemWrapper;
import org.betonquest.betonquest.api.quest.TypeFactory;

/**
 * Factory to create {@link AtlasQuestItem}s from {@link Instruction}s.
 */
public class AtlasQuestItemFactory implements TypeFactory<QuestItemWrapper> {

    private final AtlasItemRegistryService registry;

    public AtlasQuestItemFactory(final AtlasItemRegistryService registry) {
        this.registry = registry;
    }

    @Override
    public QuestItemWrapper parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<String> material = instruction.string().get();
        return new AtlasQuestItemWrapper(registry, material);
    }
}
