package org.betonquest.betonquest.api.quest;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.kernel.FactoryRegistry;
import org.betonquest.betonquest.api.kernel.QuestTypeRegistry;
import org.betonquest.betonquest.api.kernel.TypeFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;

public interface QuestTypeRegistries {
    QuestTypeRegistry<PlayerCondition, PlayerlessCondition, ?> condition();

    QuestTypeRegistry<PlayerEvent, PlayerlessEvent, ?> event();

    FactoryRegistry<TypeFactory<Objective>> objective();

    QuestTypeRegistry<PlayerVariable, PlayerlessVariable, ?> variable();
}
