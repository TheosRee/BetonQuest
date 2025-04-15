package org.betonquest.betonquest.compatibility.simplemagic;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.kernel.registry.quest.ConditionTypeRegistry;

@SuppressWarnings("PMD.CommentRequired")
public class SimpleMagicIntegrator implements Integrator {

    public SimpleMagicIntegrator() {
    }

    @SuppressWarnings("PMD.CommentRequired")
    @Override
    public void hook() {
        final ConditionTypeRegistry conditionTypes = BetonQuest.getInstance().getQuestRegistries().condition();
        final SimpleMagicService simpleMagic = BetonQuest.simpleMagic();
        conditionTypes.register("hasdud", new HasDudConditionFactory(simpleMagic));
        conditionTypes.register("hasspell", new HasSpellConditionFactory(simpleMagic));
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
