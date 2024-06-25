package org.betonquest.betonquest.quest.registry.processor;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.id.ConditionID;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;

public interface TrippleWrapper<S, P> {
    Instruction instruction();

    ConditionID[] conditions();

    @Contract(pure = true)
    @Nullable
    S playerlessType();

    @Contract(pure = true)
    @Nullable
    P playerType();
}
