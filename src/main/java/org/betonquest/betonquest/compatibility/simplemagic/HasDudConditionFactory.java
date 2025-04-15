package org.betonquest.betonquest.compatibility.simplemagic;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.instruction.Instruction;

@SuppressWarnings("PMD.CommentRequired")
public class HasDudConditionFactory implements PlayerConditionFactory {
    private final SimpleMagicService simpleMagic;

    public HasDudConditionFactory(final SimpleMagicService simpleMagic) {
        this.simpleMagic = simpleMagic;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final int count = instruction.getInt();
        if (count <= 0) {
            throw new QuestException("Can't give less than one spellbook!");
        }
        return new OnlineConditionAdapter(new HasDudCondition(simpleMagic, count), profile -> {
            throw new QuestException("Can only be checked with a player");
        });
    }
}
